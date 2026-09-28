package com.gabriel.springboot.app.menuflow.models.dto.request.invoice;

import com.gabriel.springboot.app.menuflow.models.entities.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;

public record CreateInvoiceRequest(
        @NotNull
        Long orderId,

        @NotNull
        PaymentMethod paymentMethod
) {
}
