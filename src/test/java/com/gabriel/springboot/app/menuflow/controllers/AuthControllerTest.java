package com.gabriel.springboot.app.menuflow.controllers;

import com.gabriel.springboot.app.menuflow.models.dto.request.auth.LoginRequest;
import com.gabriel.springboot.app.menuflow.models.dto.request.auth.RegisterRequest;
import com.gabriel.springboot.app.menuflow.models.dto.request.table.TableLoginRequest;
import com.gabriel.springboot.app.menuflow.models.dto.response.auth.AuthResponse;
import com.gabriel.springboot.app.menuflow.security.JwtUtil;
import com.gabriel.springboot.app.menuflow.config.JacksonConfig;
import com.gabriel.springboot.app.menuflow.security.SpringSecurityConfig;
import com.gabriel.springboot.app.menuflow.exceptions.handler.CustomAuthenticationEntryPoint;
import com.gabriel.springboot.app.menuflow.exceptions.handler.CustomAccessDenied;
import com.gabriel.springboot.app.menuflow.services.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static com.gabriel.springboot.app.menuflow.constants.ApiResponseMessages.REGISTRATION_SUCCESSFULLY;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({SpringSecurityConfig.class, CustomAuthenticationEntryPoint.class, CustomAccessDenied.class, JacksonConfig.class})
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private AuthService authService;

    @Test
    @DisplayName("Should authenticate user and return JWT token when credentials are valid")
    void login() throws Exception {
        LoginRequest request = new LoginRequest("Gabriel", "gabriel1234");

        AuthResponse authResponse = AuthResponse.builder()
                .token("token-abc-123")
                .username("gabriel")
                .roles(List.of("ROLE_USER"))
                .build();

        when(authService.login(any(LoginRequest.class))).thenReturn(authResponse);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.token").value("token-abc-123"))
                .andExpect(jsonPath("$.data.username").value("gabriel"))
                .andExpect(jsonPath("$.data.roles").value("ROLE_USER"));

        verify(authService, times(1)).login(any(LoginRequest.class));
    }

    @Test
    @DisplayName("Should return 400 Bad Request when login request is invalid")
    void shouldFailLoginWhenInvalidRequest() throws Exception {

        LoginRequest request = new LoginRequest("", "");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should authenticate table using QR code and return JWT token")
    void loginTable() throws Exception {
        TableLoginRequest request = new TableLoginRequest("QR123");

        AuthResponse response =  AuthResponse.builder()
                .token("token-table")
                .username("gabriel")
                .roles(List.of("ROLE_USER"))
                .build();

        when(authService.loginTable(any(TableLoginRequest.class))).thenReturn(response);
        mockMvc.perform(post("/api/auth/table")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.token").value("token-table"))
                .andExpect(jsonPath("$.data.username").value("gabriel"));
    }

    @Test
    @DisplayName("Should register a new user successfully and return 201 Created")
    void register() throws Exception {
        RegisterRequest request = new RegisterRequest("Gabriel", "test@email.com", "gabriel123");

        doNothing().when(authService).register(any(RegisterRequest.class));

        mockMvc.perform(post("/api/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.message") . value(REGISTRATION_SUCCESSFULLY + request.username()));
    }
}