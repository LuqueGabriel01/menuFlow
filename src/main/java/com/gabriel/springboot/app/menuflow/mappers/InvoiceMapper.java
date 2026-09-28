package com.gabriel.springboot.app.menuflow.mappers;

import com.gabriel.springboot.app.menuflow.models.dto.response.invoice.InvoiceResponse;
import com.gabriel.springboot.app.menuflow.models.entities.Invoice;
import org.springframework.stereotype.Component;

@Component
public class InvoiceMapper {
    public InvoiceResponse toResponse(Invoice invoice) {
        return InvoiceResponse.builder()
                .id(invoice.getId())
                .orderId(invoice.getOrder().getId())
                .sessionId(invoice.getOrder().getTableSession().getId())
                .tableId(invoice.getOrder().getTableSession().getDiningTable().getId())
                .tableNumber(invoice.getOrder().getTableSession().getDiningTable().getNumber())
                .totalAmount(invoice.getTotalAmount())
                .paymentMethod(invoice.getPaymentMethod())
                .paidAt(invoice.getPaidAt())
                .createdBy(invoice.getCreatedBy() != null ? invoice.getCreatedBy().getUsername() : null)
                .build();
    }
}
