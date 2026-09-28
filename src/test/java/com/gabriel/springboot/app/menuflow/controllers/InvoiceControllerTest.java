package com.gabriel.springboot.app.menuflow.controllers;

import com.gabriel.springboot.app.menuflow.models.dto.request.invoice.CreateInvoiceRequest;
import com.gabriel.springboot.app.menuflow.models.dto.response.invoice.InvoiceResponse;
import com.gabriel.springboot.app.menuflow.models.entities.enums.PaymentMethod;
import com.gabriel.springboot.app.menuflow.security.JwtUtil;
import com.gabriel.springboot.app.menuflow.services.InvoiceService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.List;

import static com.gabriel.springboot.app.menuflow.constants.ApiPaths.INVOICE_PATH;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(InvoiceController.class)
@AutoConfigureMockMvc(addFilters = false)
class InvoiceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private InvoiceService invoiceService;

    private InvoiceResponse sampleResponse() {
        return InvoiceResponse.builder()
                .id(1L)
                .orderId(1L)
                .sessionId(1L)
                .tableId(1L)
                .tableNumber(5)
                .totalAmount(BigDecimal.TEN)
                .paymentMethod(PaymentMethod.CASH)
                .createdBy("cashier1")
                .build();
    }

    @Test
    @DisplayName("Should return a list of all invoices with 200 OK")
    void getAllInvoices() throws Exception {
        when(invoiceService.getAllInvoices()).thenReturn(List.of(sampleResponse()));

        mockMvc.perform(get(INVOICE_PATH))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @DisplayName("Should return invoices filtered by session")
    void getInvoicesBySession() throws Exception {
        when(invoiceService.getInvoicesBySession(1L)).thenReturn(List.of(sampleResponse()));

        mockMvc.perform(get(INVOICE_PATH + "/session/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].sessionId").value(1));
    }

    @Test
    @DisplayName("Should return the invoice of an order")
    void getInvoiceByOrder() throws Exception {
        when(invoiceService.getInvoiceByOrder(1L)).thenReturn(sampleResponse());

        mockMvc.perform(get(INVOICE_PATH + "/order/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.orderId").value(1));
    }

    @Test
    @DisplayName("Should return invoice details when a valid ID is provided")
    void getInvoice() throws Exception {
        when(invoiceService.getInvoiceById(1L)).thenReturn(sampleResponse());

        mockMvc.perform(get(INVOICE_PATH + "/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    @DisplayName("Should create a new invoice and return 201 Created")
    void createInvoice() throws Exception {
        CreateInvoiceRequest request = new CreateInvoiceRequest(1L, PaymentMethod.CASH);

        when(invoiceService.createInvoice(request)).thenReturn(sampleResponse());

        mockMvc.perform(post(INVOICE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.paymentMethod").value("CASH"));
    }
}
