package com.gabriel.springboot.app.menuflow.mappers;

import com.gabriel.springboot.app.menuflow.models.dto.response.OrderSummaryResponse;
import com.gabriel.springboot.app.menuflow.models.entities.Order;
import org.springframework.stereotype.Component;

@Component
public class OrderMapper {
    public OrderSummaryResponse convertToOrderSummary(Order order) {
        return OrderSummaryResponse.builder()
                .id(order.getId())
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .itemCount(order.getOrderItems() != null ? order.getOrderItems().size() : 0)
                .createdAt(order.getCreatedAt())
                .build();
    }
}
