package com.gabriel.springboot.app.menuflow.services;

import com.gabriel.springboot.app.menuflow.models.dto.request.order.CreateOrderRequest;
import com.gabriel.springboot.app.menuflow.models.dto.request.order.OrderItemRequest;
import com.gabriel.springboot.app.menuflow.models.dto.request.order.UpdateOrderStatusRequest;
import com.gabriel.springboot.app.menuflow.models.dto.response.order.OrderResponse;

import java.util.List;

public interface OrderService {
    OrderResponse createOrder(CreateOrderRequest request);
    OrderResponse addItem(Long orderId, OrderItemRequest request);
    OrderResponse removeItem(Long orderId, Long itemId);
    OrderResponse updateStatus(Long orderId, UpdateOrderStatusRequest request);
    OrderResponse getOrderById(Long id);
    List<OrderResponse> getOrdersBySession(Long sessionId);
    List<OrderResponse> getActiveOrders();
    List<OrderResponse> getKitchenQueue();
    OrderResponse cancelOrder(Long id);
}
