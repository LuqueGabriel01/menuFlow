package com.gabriel.springboot.app.menuflow.models.dto.request.order;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record OrderItemRequest(
        @NotNull
        Long dishId,

        @NotNull
        @Min(1)
        Integer quantity
) {
}
