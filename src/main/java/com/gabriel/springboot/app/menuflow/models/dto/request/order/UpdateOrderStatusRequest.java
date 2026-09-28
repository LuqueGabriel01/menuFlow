package com.gabriel.springboot.app.menuflow.models.dto.request.order;

import com.gabriel.springboot.app.menuflow.models.entities.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateOrderStatusRequest(
        @NotNull
        OrderStatus status
) {
}
