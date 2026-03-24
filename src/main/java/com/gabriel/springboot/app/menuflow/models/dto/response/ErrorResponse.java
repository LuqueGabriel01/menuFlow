package com.gabriel.springboot.app.menuflow.models.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Map;

@Builder
public record ErrorResponse(
        LocalDateTime date,
        int status,
        String error,
        String message,
        String path,
        Map<String, String> validationErrors
) {
    public ErrorResponse{
        date = LocalDateTime.now();
    }
}
