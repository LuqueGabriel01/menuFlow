package com.gabriel.springboot.app.menuflow.services.impl;

import com.gabriel.springboot.app.menuflow.exceptions.ResourceNotFoundException;
import com.gabriel.springboot.app.menuflow.mappers.UserMapper;
import com.gabriel.springboot.app.menuflow.models.dto.request.auth.LoginRequest;
import com.gabriel.springboot.app.menuflow.models.dto.request.auth.RegisterRequest;
import com.gabriel.springboot.app.menuflow.models.dto.request.table.TableLoginRequest;
import com.gabriel.springboot.app.menuflow.models.dto.response.auth.AuthResponse;
import com.gabriel.springboot.app.menuflow.models.entities.DiningTable;
import com.gabriel.springboot.app.menuflow.models.entities.Role;
import com.gabriel.springboot.app.menuflow.models.entities.TableSession;
import com.gabriel.springboot.app.menuflow.models.entities.User;
import com.gabriel.springboot.app.menuflow.models.entities.enums.RoleName;
import com.gabriel.springboot.app.menuflow.models.entities.enums.SessionStatus;
import com.gabriel.springboot.app.menuflow.repositories.DiningTableRepository;
import com.gabriel.springboot.app.menuflow.repositories.RoleRepository;
import com.gabriel.springboot.app.menuflow.repositories.TableSessionRepository;
import com.gabriel.springboot.app.menuflow.repositories.UserRepository;
import com.gabriel.springboot.app.menuflow.security.JwtUtil;
import com.gabriel.springboot.app.menuflow.services.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.gabriel.springboot.app.menuflow.exceptions.BadRequestException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.gabriel.springboot.app.menuflow.constants.ApiResponseMessages.*;
import static com.gabriel.springboot.app.menuflow.constants.ExceptionConstants.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final TableSessionRepository tableSessionRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final DiningTableRepository diningTableRepository;
    private final JwtUtil jwtUtil;
    private final UserMapper userMapper;

    @Override
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new BadCredentialsException(USER_NOT_FOUND_MESSAGE));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BadCredentialsException(INVALID_CREDENTIALS_MESSAGE);
        }

        if (!user.isEnabled()) {
            throw new DisabledException(USER_DISABLED_MESSAGE);
        }

        Collection<GrantedAuthority> authorities = convertRolesToAuthorities(user.getRoles());

        String token = jwtUtil.generateToken(user.getUsername(), authorities);

        List<String> roles = extractRoleNames(user.getRoles());

        return AuthResponse.builder()
                .token(token)
                .username(user.getUsername())
                .roles(roles)
                .build();
    }

    @Override
    @Transactional
    public AuthResponse loginTable(TableLoginRequest request) {

        DiningTable table = diningTableRepository.findByQrCode(request.qrCode())
                .orElseThrow(() -> new ResourceNotFoundException(TABLE_NOT_FOUND_MESSAGE));

        if (!table.isActive()) {
            throw new ResourceNotFoundException(TABLE_NOT_AVAILABLE_MESSAGE);
        }

        TableSession session = tableSessionRepository.findByDiningTableIdAndStatus(table.getId(), SessionStatus.OPEN)
                .orElseGet(() -> tableSessionRepository.save(TableSession.of(table)));

        String subject = PREFIX_TABLE + table.getQrCode();

        Collection<GrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority(RoleName.ROLE_USER.name())
        );

        String token = jwtUtil.generateToken(subject, authorities);

        log.info("QR code login successful - table: {}, Session: {}", table.getNumber(), session.getId());

        return AuthResponse.builder()
                .token(token)
                .username(PREFIX_TABLE + table.getNumber())
                .roles(List.of(RoleName.ROLE_USER.name()))
                .build();
    }

    @Override
    @Transactional
    public void register(RegisterRequest request){

        if (userRepository.existsByUsername(request.username())) {
            throw new BadRequestException("That username is already taken");
        }

        String encodedPassword = passwordEncoder.encode(request.password());

        Role userRole = roleRepository.findByName(RoleName.ROLE_USER).orElseThrow();

        User user = userMapper.toEntity(request, encodedPassword, userRole);

        userRepository.save(user);
        log.info("Newly registered user: {}", user.getUsername());
    }

    private Collection<GrantedAuthority> convertRolesToAuthorities(Set<Role> roles) {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                .collect(Collectors.toList());
    }

    private List<String> extractRoleNames(Set<Role> roles) {
        return roles.stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toList());
    }
}
