package com.gabriel.springboot.app.menuflow.models.dto.request.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CreateOrderRequest(
        @NotNull
        Long sessionId,

        @NotEmpty
        @Valid
        List<OrderItemRequest> items
) {
}
