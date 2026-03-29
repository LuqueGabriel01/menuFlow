package com.gabriel.springboot.app.menuflow.models.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record TableResponse(
        Long id,
        Integer number,
        Boolean active,
        String qrCode,
        LocalDateTime createdAt,
        Boolean hasActiveSession
) {
    public TableResponse {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
