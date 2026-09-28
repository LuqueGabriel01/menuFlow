package com.gabriel.springboot.app.menuflow.services.impl;

import com.gabriel.springboot.app.menuflow.exceptions.BusinessException;
import com.gabriel.springboot.app.menuflow.exceptions.ResourceNotFoundException;
import com.gabriel.springboot.app.menuflow.mappers.InvoiceMapper;
import com.gabriel.springboot.app.menuflow.models.dto.request.invoice.CreateInvoiceRequest;
import com.gabriel.springboot.app.menuflow.models.dto.response.invoice.InvoiceResponse;
import com.gabriel.springboot.app.menuflow.models.entities.DiningTable;
import com.gabriel.springboot.app.menuflow.models.entities.Invoice;
import com.gabriel.springboot.app.menuflow.models.entities.Order;
import com.gabriel.springboot.app.menuflow.models.entities.TableSession;
import com.gabriel.springboot.app.menuflow.models.entities.enums.PaymentMethod;
import com.gabriel.springboot.app.menuflow.repositories.InvoiceRepository;
import com.gabriel.springboot.app.menuflow.repositories.OrderRepository;
import com.gabriel.springboot.app.menuflow.repositories.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InvoiceServiceImplTest {

    @Mock
    private InvoiceRepository invoiceRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private InvoiceMapper invoiceMapper;

    @InjectMocks
    private InvoiceServiceImpl invoiceService;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private Order servedOrder() {
        DiningTable table = DiningTable.of(5, "qr-123");
        TableSession session = TableSession.of(table);
        Order order = Order.of(session);
        order.inProgress();
        order.ready();
        order.served();
        return order;
    }

    @Test
    @DisplayName("Should create invoice successfully for a served order without a previous invoice")
    void createInvoice() {
        CreateInvoiceRequest request = new CreateInvoiceRequest(1L, PaymentMethod.CASH);

        Order order = servedOrder();
        Invoice invoice = Invoice.of(order);
        InvoiceResponse response = InvoiceResponse.builder().id(1L).build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(invoiceRepository.save(any(Invoice.class))).thenReturn(invoice);
        when(invoiceMapper.toResponse(invoice)).thenReturn(response);

        InvoiceResponse result = invoiceService.createInvoice(request);

        assertNotNull(result);
        verify(invoiceRepository).save(any(Invoice.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when order does not exist")
    void createInvoiceOrderNotFound() {
        CreateInvoiceRequest request = new CreateInvoiceRequest(1L, PaymentMethod.CASH);

        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> invoiceService.createInvoice(request));

        verify(invoiceRepository, never()).save(any(Invoice.class));
    }

    @Test
    @DisplayName("Should throw BusinessException when order is not served yet")
    void createInvoiceOrderNotServed() {
        CreateInvoiceRequest request = new CreateInvoiceRequest(1L, PaymentMethod.CASH);

        DiningTable table = DiningTable.of(5, "qr-123");
        TableSession session = TableSession.of(table);
        Order order = Order.of(session);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThrows(BusinessException.class, () -> invoiceService.createInvoice(request));

        verify(invoiceRepository, never()).save(any(Invoice.class));
    }

    @Test
    @DisplayName("Should throw BusinessException when order already has an invoice")
    void createInvoiceAlreadyInvoiced() {
        CreateInvoiceRequest request = new CreateInvoiceRequest(1L, PaymentMethod.CASH);

        Order order = servedOrder();
        Invoice existing = Invoice.of(order);
        order.assignInvoice(existing);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThrows(BusinessException.class, () -> invoiceService.createInvoice(request));

        verify(invoiceRepository, never()).save(any(Invoice.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when invoice does not exist")
    void getInvoiceByIdNotFound() {
        when(invoiceRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> invoiceService.getInvoiceById(1L));
    }

    @Test
    @DisplayName("Should return invoice for a given order")
    void getInvoiceByOrder() {
        Order order = servedOrder();
        Invoice invoice = Invoice.of(order);
        InvoiceResponse response = InvoiceResponse.builder().id(1L).build();

        when(invoiceRepository.findByOrderId(1L)).thenReturn(Optional.of(invoice));
        when(invoiceMapper.toResponse(invoice)).thenReturn(response);

        InvoiceResponse result = invoiceService.getInvoiceByOrder(1L);

        assertNotNull(result);
    }

    @Test
    @DisplayName("Should return list of invoices for a session")
    void getInvoicesBySession() {
        when(invoiceRepository.findByOrder_TableSession_Id(1L)).thenReturn(List.of());

        List<InvoiceResponse> result = invoiceService.getInvoicesBySession(1L);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should return list of all invoices")
    void getAllInvoices() {
        when(invoiceRepository.findAll()).thenReturn(List.of());

        List<InvoiceResponse> result = invoiceService.getAllInvoices();

        assertTrue(result.isEmpty());
    }
}
