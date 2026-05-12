package com.gabriel.springboot.app.menuflow.models.dto.response;

import com.gabriel.springboot.app.menuflow.models.entities.enums.OrderStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record OrderSummaryResponse(
        Long id,
        OrderStatus status,
        BigDecimal totalAmount,
        Integer itemCount,
        LocalDateTime createdAt
) {
    public OrderSummaryResponse {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
