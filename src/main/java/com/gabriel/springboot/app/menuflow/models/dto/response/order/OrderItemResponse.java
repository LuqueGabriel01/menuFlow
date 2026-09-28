package com.gabriel.springboot.app.menuflow.models.dto.response.order;

import com.gabriel.springboot.app.menuflow.models.entities.enums.OrderStatus;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record OrderItemResponse(
        Long id,
        Long dishId,
        String dishName,
        Integer quantity,
        BigDecimal price,
        OrderStatus status
) {
}
