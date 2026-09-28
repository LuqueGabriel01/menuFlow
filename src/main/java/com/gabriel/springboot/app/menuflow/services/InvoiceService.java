package com.gabriel.springboot.app.menuflow.services;

import com.gabriel.springboot.app.menuflow.models.dto.request.invoice.CreateInvoiceRequest;
import com.gabriel.springboot.app.menuflow.models.dto.response.invoice.InvoiceResponse;

import java.util.List;

public interface InvoiceService {
    InvoiceResponse createInvoice(CreateInvoiceRequest request);
    InvoiceResponse getInvoiceById(Long id);
    InvoiceResponse getInvoiceByOrder(Long orderId);
    List<InvoiceResponse> getInvoicesBySession(Long sessionId);
    List<InvoiceResponse> getAllInvoices();
}
