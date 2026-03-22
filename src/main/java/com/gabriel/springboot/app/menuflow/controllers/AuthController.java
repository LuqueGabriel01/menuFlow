package com.gabriel.springboot.app.menuflow.controllers;

import com.gabriel.springboot.app.menuflow.models.dto.request.LoginRequest;
import com.gabriel.springboot.app.menuflow.models.dto.request.RegisterRequest;
import com.gabriel.springboot.app.menuflow.models.dto.request.TableLoginRequest;
import com.gabriel.springboot.app.menuflow.models.dto.response.ApiResponse;
import com.gabriel.springboot.app.menuflow.models.dto.response.AuthResponse;
import com.gabriel.springboot.app.menuflow.models.dto.response.MessageResponse;
import com.gabriel.springboot.app.menuflow.services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.gabriel.springboot.app.menuflow.constants.ApiPaths.*;
import static com.gabriel.springboot.app.menuflow.constants.ApiResponseMessages.REGISTRATION_SUCCESSFULLY;

@Slf4j
@RestController
@RequestMapping(AUTH_PATH)
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication and login endpoints")
public class AuthController {

    private final AuthService authService;

    @PostMapping(LOGIN)
    @Operation(
            summary = "User login",
            description = "Authenticate a user using a username and password, and return a JWT token"
    )
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        log.info("POST  /api/auth/login - Login Request: {}", request.username());
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping(TABLE)
    @Operation(
            summary = "Login table using QR",
            description = "Scan a table's QR code to log in, or create or reuse a session"
    )
    public ResponseEntity<ApiResponse<AuthResponse>> loginTable(@Valid @RequestBody TableLoginRequest request) {
        log.info("POST /api/auth/table - qrCode: {}", request.qrCode());
        AuthResponse response = authService.loginTable(request);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping(REGISTER)
    @Operation(summary = "Register a new user")
    public ResponseEntity<ApiResponse<MessageResponse>> register(@Valid @RequestBody RegisterRequest request) {

        authService.register(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        new MessageResponse(REGISTRATION_SUCCESSFULLY + request.username())));
    }
}
