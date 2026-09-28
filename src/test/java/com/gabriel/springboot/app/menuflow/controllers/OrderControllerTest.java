package com.gabriel.springboot.app.menuflow.controllers;

import com.gabriel.springboot.app.menuflow.models.dto.request.order.CreateOrderRequest;
import com.gabriel.springboot.app.menuflow.models.dto.request.order.OrderItemRequest;
import com.gabriel.springboot.app.menuflow.models.dto.request.order.UpdateOrderStatusRequest;
import com.gabriel.springboot.app.menuflow.models.dto.response.order.OrderResponse;
import com.gabriel.springboot.app.menuflow.models.entities.enums.OrderStatus;
import com.gabriel.springboot.app.menuflow.security.JwtUtil;
import com.gabriel.springboot.app.menuflow.services.OrderService;
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
import java.util.Collections;
import java.util.List;

import static com.gabriel.springboot.app.menuflow.constants.ApiPaths.ORDER_PATH;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
@AutoConfigureMockMvc(addFilters = false)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private OrderService orderService;

    private OrderResponse sampleResponse() {
        return OrderResponse.builder()
                .id(1L)
                .sessionId(1L)
                .tableId(1L)
                .tableNumber(5)
                .status(OrderStatus.PENDING)
                .totalAmount(BigDecimal.TEN)
                .items(Collections.emptyList())
                .build();
    }

    @Test
    @DisplayName("Should create a new order and return 201 Created")
    void createOrder() throws Exception {
        CreateOrderRequest request = new CreateOrderRequest(1L, List.of(new OrderItemRequest(1L, 2)));

        when(orderService.createOrder(request)).thenReturn(sampleResponse());

        mockMvc.perform(post(ORDER_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.status").value("PENDING"));
    }

    @Test
    @DisplayName("Should return active orders with 200 OK")
    void getActiveOrders() throws Exception {
        when(orderService.getActiveOrders()).thenReturn(List.of(sampleResponse()));

        mockMvc.perform(get(ORDER_PATH + "/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @DisplayName("Should return orders filtered by session")
    void getOrdersBySession() throws Exception {
        when(orderService.getOrdersBySession(1L)).thenReturn(List.of(sampleResponse()));

        mockMvc.perform(get(ORDER_PATH + "/session/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].sessionId").value(1));
    }

    @Test
    @DisplayName("Should return order details when a valid ID is provided")
    void getOrder() throws Exception {
        when(orderService.getOrderById(1L)).thenReturn(sampleResponse());

        mockMvc.perform(get(ORDER_PATH + "/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    @DisplayName("Should add an item to an order and return 200 OK")
    void addItem() throws Exception {
        OrderItemRequest request = new OrderItemRequest(2L, 1);

        when(orderService.addItem(eq(1L), any())).thenReturn(sampleResponse());

        mockMvc.perform(post(ORDER_PATH + "/1/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    @DisplayName("Should remove an item from an order and return 200 OK")
    void removeItem() throws Exception {
        when(orderService.removeItem(1L, 2L)).thenReturn(sampleResponse());

        mockMvc.perform(delete(ORDER_PATH + "/1/items/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    @DisplayName("Should update order status and return 200 OK")
    void updateStatus() throws Exception {
        UpdateOrderStatusRequest request = new UpdateOrderStatusRequest(OrderStatus.IN_PROGRESS);

        when(orderService.updateStatus(eq(1L), any())).thenReturn(sampleResponse());

        mockMvc.perform(patch(ORDER_PATH + "/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    @DisplayName("Should cancel an order and return 200 OK")
    void cancelOrder() throws Exception {
        when(orderService.cancelOrder(1L)).thenReturn(sampleResponse());

        mockMvc.perform(delete(ORDER_PATH + "/1/cancel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1));
    }
}
