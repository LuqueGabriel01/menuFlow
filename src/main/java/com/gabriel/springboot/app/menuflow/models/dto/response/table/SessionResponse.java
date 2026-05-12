package com.gabriel.springboot.app.menuflow.models.dto.response;

import com.gabriel.springboot.app.menuflow.models.entities.enums.SessionStatus;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record SessionResponse(
        Long id,
        Long tableId,
        Integer tableNumber,
        LocalDateTime openedAt,
        LocalDateTime closedAt,
        SessionStatus status,
        String openedBy,
        String duration
) {
    public SessionResponse {
        if (openedAt == null) {
            openedAt = LocalDateTime.now();
        }
    }
}
