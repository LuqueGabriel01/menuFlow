package com.gabriel.springboot.app.menuflow.mappers;

import com.gabriel.springboot.app.menuflow.models.dto.response.order.OrderItemResponse;
import com.gabriel.springboot.app.menuflow.models.dto.response.order.OrderResponse;
import com.gabriel.springboot.app.menuflow.models.dto.response.table.OrderSummaryResponse;
import com.gabriel.springboot.app.menuflow.models.entities.Order;
import com.gabriel.springboot.app.menuflow.models.entities.OrderItem;
import org.springframework.stereotype.Component;

import static com.gabriel.springboot.app.menuflow.constants.LocaleConstants.DEFAULT_LANG;

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

    public OrderResponse toResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .sessionId(order.getTableSession().getId())
                .tableId(order.getTableSession().getDiningTable().getId())
                .tableNumber(order.getTableSession().getDiningTable().getNumber())
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .createdAt(order.getCreatedAt())
                .closedAt(order.getClosedAt())
                .items(order.getOrderItems().stream().map(this::toItemResponse).toList())
                .build();
    }

    private OrderItemResponse toItemResponse(OrderItem item) {
        return OrderItemResponse.builder()
                .id(item.getId())
                .dishId(item.getDish().getId())
                .dishName(item.getDish().getName(DEFAULT_LANG))
                .quantity(item.getQuantity())
                .price(item.getPrice())
                .status(item.getStatus())
                .build();
    }
}
