package com.gabriel.springboot.app.menuflow.services.impl;

import com.gabriel.springboot.app.menuflow.exceptions.BusinessException;
import com.gabriel.springboot.app.menuflow.exceptions.ResourceNotFoundException;
import com.gabriel.springboot.app.menuflow.mappers.InvoiceMapper;
import com.gabriel.springboot.app.menuflow.models.dto.request.invoice.CreateInvoiceRequest;
import com.gabriel.springboot.app.menuflow.models.dto.response.invoice.InvoiceResponse;
import com.gabriel.springboot.app.menuflow.models.entities.Invoice;
import com.gabriel.springboot.app.menuflow.models.entities.Order;
import com.gabriel.springboot.app.menuflow.models.entities.User;
import com.gabriel.springboot.app.menuflow.models.entities.enums.OrderStatus;
import com.gabriel.springboot.app.menuflow.repositories.InvoiceRepository;
import com.gabriel.springboot.app.menuflow.repositories.OrderRepository;
import com.gabriel.springboot.app.menuflow.repositories.UserRepository;
import com.gabriel.springboot.app.menuflow.services.InvoiceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.gabriel.springboot.app.menuflow.constants.ExceptionConstants.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final InvoiceMapper invoiceMapper;

    @Override
    @Transactional
    public InvoiceResponse createInvoice(CreateInvoiceRequest request) {
        log.info("create invoice for order: {}", request.orderId());

        Order order = orderRepository.findById(request.orderId())
                .orElseThrow(() -> new ResourceNotFoundException(ORDER_NOT_FOUND_MESSAGE));

        if (order.getInvoice() != null) {
            throw new BusinessException(INVOICE_ALREADY_EXISTS_MESSAGE);
        }

        if (order.getStatus() != OrderStatus.SERVED) {
            throw new BusinessException(ORDER_NOT_READY_FOR_INVOICE_MESSAGE);
        }

        Invoice invoice = Invoice.of(order);
        order.assignInvoice(invoice);

        applyPaymentMethod(invoice, request);
        invoice.assignUser(getCurrentUser());

        Invoice saved = invoiceRepository.save(invoice);

        log.info("create invoice successful - ID: {}, order: {}", saved.getId(), order.getId());

        return invoiceMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public InvoiceResponse getInvoiceById(Long id) {
        return invoiceRepository.findById(id)
                .map(invoiceMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException(INVOICE_NOT_FOUND_MESSAGE));
    }

    @Override
    @Transactional(readOnly = true)
    public InvoiceResponse getInvoiceByOrder(Long orderId) {
        return invoiceRepository.findByOrderId(orderId)
                .map(invoiceMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException(INVOICE_NOT_FOUND_MESSAGE));
    }

    @Override
    @Transactional(readOnly = true)
    public List<InvoiceResponse> getInvoicesBySession(Long sessionId) {
        return invoiceRepository.findByOrder_TableSession_Id(sessionId).stream()
                .map(invoiceMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<InvoiceResponse> getAllInvoices() {
        return invoiceRepository.findAll().stream()
                .map(invoiceMapper::toResponse)
                .toList();
    }

    private void applyPaymentMethod(Invoice invoice, CreateInvoiceRequest request) {
        switch (request.paymentMethod()) {
            case CASH -> invoice.cash();
            case CARD -> invoice.card();
            case TRANSFER -> invoice.transfer();
        }
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return null;
        }
        return userRepository.findByUsername(authentication.getName()).orElse(null);
    }
}
