package com.gabriel.springboot.app.menuflow.services.impl;

import com.gabriel.springboot.app.menuflow.models.dto.request.auth.LoginRequest;
import com.gabriel.springboot.app.menuflow.models.dto.response.auth.AuthResponse;
import com.gabriel.springboot.app.menuflow.models.entities.Role;
import com.gabriel.springboot.app.menuflow.models.entities.User;
import com.gabriel.springboot.app.menuflow.models.entities.enums.RoleName;
import com.gabriel.springboot.app.menuflow.repositories.UserRepository;
import com.gabriel.springboot.app.menuflow.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthServiceImpl authService;

    private User mockUser;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        loginRequest = new LoginRequest("gabriel", "12345");

        mockUser = User.of("gabriel", "gabriel@mail.com");
        mockUser.updatePassword("encoded_password");

        Role adminRole = Role.of(RoleName.ROLE_ADMIN);
        mockUser.addRole(adminRole);
    }

    @Test
    @DisplayName("Login successful: Must return an AuthResponse with a token")
    void login_Success() {

        when(userRepository.findByUsername("gabriel")).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches("12345", "encoded_password")).thenReturn(true);
        when(jwtUtil.generateToken(anyString(), anyCollection())).thenReturn("mocked-jwt-token");

        AuthResponse response = authService.login(loginRequest);

        assertNotNull(response);
        assertEquals("mocked-jwt-token", response.token());
        assertEquals("gabriel", response.username());
        assertTrue(response.roles().contains("ROLE_ADMIN"));
        verify(userRepository).findByUsername("gabriel");
    }

    @Test
    @DisplayName("Login failed: User does not exist throws BadCredentialsException")
    void login_UserNotFound_ThrowsException() {

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        assertThrows(BadCredentialsException.class, () -> authService.login(loginRequest));
    }

    @Test
    @DisplayName("Login failed: Incorrect password throws a BadCredentialsException")
    void login_WrongPassword_ThrowsException() {

        when(userRepository.findByUsername("gabriel")).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        BadCredentialsException exception = assertThrows(BadCredentialsException.class,
                () -> authService.login(loginRequest));

        assertEquals("Invalid username or password", exception.getMessage());
    }
}

