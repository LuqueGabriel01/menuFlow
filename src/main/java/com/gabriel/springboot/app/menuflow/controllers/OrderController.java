package com.gabriel.springboot.app.menuflow.controllers;

import com.gabriel.springboot.app.menuflow.models.dto.request.order.CreateOrderRequest;
import com.gabriel.springboot.app.menuflow.models.dto.request.order.OrderItemRequest;
import com.gabriel.springboot.app.menuflow.models.dto.request.order.UpdateOrderStatusRequest;
import com.gabriel.springboot.app.menuflow.models.dto.response.ApiResponse;
import com.gabriel.springboot.app.menuflow.models.dto.response.order.OrderResponse;
import com.gabriel.springboot.app.menuflow.services.OrderService;
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
@RequestMapping(ORDER_PATH)
@Tag(name = "Order Management", description = "Endpoints for placing and tracking orders")
@SecurityRequirement(name = SECURITY_SCHEME_NAME)
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @Operation(
            summary = "Create order",
            description = "Creates a new order with one or more dishes for an open table session"
    )
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        log.info("POST: {} - create order for session: {}", ORDER_PATH, request.sessionId());

        OrderResponse response = orderService.createOrder(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    @GetMapping(ACTIVE)
    @Operation(
            summary = "List active orders",
            description = "Kitchen queue: orders that are PENDING, IN_PROGRESS or READY. ADMIN, CHEF and KITCHEN only."
    )
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getActiveOrders() {
        log.info("GET: {} - get active orders", ORDER_PATH + ACTIVE);

        List<OrderResponse> orders = orderService.getActiveOrders();

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(orders));
    }

    @GetMapping(PATH_SESSION_ID)
    @Operation(summary = "List orders of a session")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getOrdersBySession(@PathVariable Long sessionId) {
        log.info("GET: {}/session/{} - get orders by session", ORDER_PATH, sessionId);

        List<OrderResponse> orders = orderService.getOrdersBySession(sessionId);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(orders));
    }

    @GetMapping(PATH_ID)
    @Operation(summary = "Get order by ID")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrder(@PathVariable Long id) {
        log.info("GET: {}/{} - get order", ORDER_PATH, id);

        OrderResponse response = orderService.getOrderById(id);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(response));
    }

    @PostMapping(PATH_ID_ITEMS)
    @Operation(summary = "Add a dish to an existing order")
    public ResponseEntity<ApiResponse<OrderResponse>> addItem(@PathVariable Long id, @Valid @RequestBody OrderItemRequest request) {
        log.info("POST: {}/{}/items - add item to order", ORDER_PATH, id);

        OrderResponse response = orderService.addItem(id, request);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(response));
    }

    @DeleteMapping(PATH_ID_ITEM_ID)
    @Operation(summary = "Remove a dish from an order")
    public ResponseEntity<ApiResponse<OrderResponse>> removeItem(@PathVariable Long id, @PathVariable Long itemId) {
        log.info("DELETE: {}/{}/items/{} - remove item from order", ORDER_PATH, id, itemId);

        OrderResponse response = orderService.removeItem(id, itemId);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(response));
    }

    @PatchMapping(PATH_ID_STATUS)
    @Operation(
            summary = "Update order status",
            description = "Moves the order through the kitchen workflow. ADMIN, CHEF and KITCHEN only."
    )
    public ResponseEntity<ApiResponse<OrderResponse>> updateStatus(@PathVariable Long id, @Valid @RequestBody UpdateOrderStatusRequest request) {
        log.info("PATCH: {}/{}/status - update order status to {}", ORDER_PATH, id, request.status());

        OrderResponse response = orderService.updateStatus(id, request);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(response));
    }

    @DeleteMapping(PATH_ID_CANCEL)
    @Operation(summary = "Cancel order")
    public ResponseEntity<ApiResponse<OrderResponse>> cancelOrder(@PathVariable Long id) {
        log.info("DELETE: {}/{}/cancel - cancel order", ORDER_PATH, id);

        OrderResponse response = orderService.cancelOrder(id);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(response));
    }
}
