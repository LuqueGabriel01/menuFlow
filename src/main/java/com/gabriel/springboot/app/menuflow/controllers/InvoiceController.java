package com.gabriel.springboot.app.menuflow.controllers;

import com.gabriel.springboot.app.menuflow.models.dto.request.invoice.CreateInvoiceRequest;
import com.gabriel.springboot.app.menuflow.models.dto.response.ApiResponse;
import com.gabriel.springboot.app.menuflow.models.dto.response.invoice.InvoiceResponse;
import com.gabriel.springboot.app.menuflow.services.InvoiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.gabriel.springboot.app.menuflow.config.SwaggerConfig.SECURITY_SCHEME_NAME;
import static com.gabriel.springboot.app.menuflow.constants.ApiPaths.*;

@Slf4j
@RestController
@RequestMapping(INVOICE_PATH)
@Tag(name = "Invoice Management", description = "Endpoints for billing and closing out orders. ADMIN and CASHIER only.")
@SecurityRequirement(name = SECURITY_SCHEME_NAME)
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;

    @GetMapping
    @Operation(summary = "List all invoices")
    public ResponseEntity<ApiResponse<List<InvoiceResponse>>> getAllInvoices() {
        log.info("GET: {} - get list of invoices", INVOICE_PATH);

        List<InvoiceResponse> invoices = invoiceService.getAllInvoices();

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(invoices));
    }

    @GetMapping(PATH_SESSION_ID)
    @Operation(summary = "List invoices of a session")
    public ResponseEntity<ApiResponse<List<InvoiceResponse>>> getInvoicesBySession(@PathVariable Long sessionId) {
        log.info("GET: {}/session/{} - get invoices by session", INVOICE_PATH, sessionId);

        List<InvoiceResponse> invoices = invoiceService.getInvoicesBySession(sessionId);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(invoices));
    }

    @GetMapping(PATH_ORDER_ID)
    @Operation(summary = "Get the invoice of an order")
    public ResponseEntity<ApiResponse<InvoiceResponse>> getInvoiceByOrder(@PathVariable Long orderId) {
        log.info("GET: {}/order/{} - get invoice by order", INVOICE_PATH, orderId);

        InvoiceResponse response = invoiceService.getInvoiceByOrder(orderId);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(response));
    }

    @GetMapping(PATH_ID)
    @Operation(summary = "Get invoice by ID")
    public ResponseEntity<ApiResponse<InvoiceResponse>> getInvoice(@PathVariable Long id) {
        log.info("GET: {}/{} - get invoice", INVOICE_PATH, id);

        InvoiceResponse response = invoiceService.getInvoiceById(id);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(response));
    }

    @PostMapping
    @Operation(
            summary = "Create invoice",
            description = "Bills a served order with a payment method and closes it"
    )
    public ResponseEntity<ApiResponse<InvoiceResponse>> createInvoice(@Valid @RequestBody CreateInvoiceRequest request) {
        log.info("POST: {} - create invoice for order: {}", INVOICE_PATH, request.orderId());

        InvoiceResponse response = invoiceService.createInvoice(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }
}
