package com.gabriel.springboot.app.menuflow.models.dto.response.invoice;

import com.gabriel.springboot.app.menuflow.models.entities.enums.PaymentMethod;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record InvoiceResponse(
        Long id,
        Long orderId,
        Long sessionId,
        Long tableId,
        Integer tableNumber,
        BigDecimal totalAmount,
        PaymentMethod paymentMethod,
        LocalDateTime paidAt,
        String createdBy
) {
}
