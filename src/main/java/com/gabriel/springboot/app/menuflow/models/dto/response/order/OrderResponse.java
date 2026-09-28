package com.gabriel.springboot.app.menuflow.models.dto.response.order;

import com.gabriel.springboot.app.menuflow.models.entities.enums.OrderStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Builder
public record OrderResponse(
        Long id,
        Long sessionId,
        Long tableId,
        Integer tableNumber,
        OrderStatus status,
        BigDecimal totalAmount,
        LocalDateTime createdAt,
        LocalDateTime closedAt,
        List<OrderItemResponse> items
) {
}
