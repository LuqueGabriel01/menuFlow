package com.gabriel.springboot.app.menuflow.exceptions.handler;

import com.gabriel.springboot.app.menuflow.models.dto.response.ErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

import static com.gabriel.springboot.app.menuflow.constants.ExceptionConstants.AUTHENTICATION_REQUIRED_MESSAGE;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");

        ErrorResponse error = ErrorResponse.builder()
                .status(HttpStatus.UNAUTHORIZED.value())
                .error(HttpStatus.UNAUTHORIZED.getReasonPhrase())
                .message(AUTHENTICATION_REQUIRED_MESSAGE)
                .path(request.getRequestURI())
                .build();

        new ObjectMapper().writeValue(response.getOutputStream(), error);
    }
}
