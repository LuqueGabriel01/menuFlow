package com.gabriel.springboot.app.menuflow.models.dto.response;

import java.time.LocalDateTime;

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
