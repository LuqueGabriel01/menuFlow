package com.gabriel.springboot.app.menuflow.services.impl;

import com.gabriel.springboot.app.menuflow.exceptions.BusinessException;
import com.gabriel.springboot.app.menuflow.exceptions.ResourceNotFoundException;
import com.gabriel.springboot.app.menuflow.mappers.OrderMapper;
import com.gabriel.springboot.app.menuflow.models.dto.request.order.CreateOrderRequest;
import com.gabriel.springboot.app.menuflow.models.dto.request.order.OrderItemRequest;
import com.gabriel.springboot.app.menuflow.models.dto.request.order.UpdateOrderStatusRequest;
import com.gabriel.springboot.app.menuflow.models.dto.response.order.OrderResponse;
import com.gabriel.springboot.app.menuflow.models.entities.Dish;
import com.gabriel.springboot.app.menuflow.models.entities.Order;
import com.gabriel.springboot.app.menuflow.models.entities.OrderItem;
import com.gabriel.springboot.app.menuflow.models.entities.TableSession;
import com.gabriel.springboot.app.menuflow.models.entities.enums.OrderStatus;
import com.gabriel.springboot.app.menuflow.models.entities.enums.SessionStatus;
import com.gabriel.springboot.app.menuflow.repositories.DishRepository;
import com.gabriel.springboot.app.menuflow.repositories.OrderRepository;
import com.gabriel.springboot.app.menuflow.repositories.TableSessionRepository;
import com.gabriel.springboot.app.menuflow.services.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

import static com.gabriel.springboot.app.menuflow.constants.ExceptionConstants.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private static final List<OrderStatus> ACTIVE_STATUSES = List.of(
            OrderStatus.PENDING, OrderStatus.IN_PROGRESS, OrderStatus.READY);

    private static final List<OrderStatus> KITCHEN_STATUSES = List.of(
            OrderStatus.PENDING, OrderStatus.IN_PROGRESS);

    private final OrderRepository orderRepository;
    private final TableSessionRepository sessionRepository;
    private final DishRepository dishRepository;
    private final OrderMapper orderMapper;

    @Override
    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        log.info("create order for session: {}", request.sessionId());

        TableSession session = sessionRepository.findById(request.sessionId())
                .orElseThrow(() -> new ResourceNotFoundException(SESSION_NOT_FOUND_MESSAGE));

        if (session.getStatus() != SessionStatus.OPEN) {
            throw new BusinessException(SESSION_NOT_ACTIVE_MESSAGE);
        }

        Order order = Order.of(session);

        request.items().forEach(item -> addOrderItem(order, item));

        order.calculateTotalAmount();

        Order saved = orderRepository.save(order);

        log.info("create order successful - ID: {}", saved.getId());

        return orderMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public OrderResponse addItem(Long orderId, OrderItemRequest request) {
        log.info("add item to order: {}", orderId);

        Order order = findOrder(orderId);

        checkModifiable(order);

        addOrderItem(order, request);
        order.calculateTotalAmount();

        Order updated = orderRepository.save(order);

        return orderMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public OrderResponse removeItem(Long orderId, Long itemId) {
        log.info("remove item {} from order: {}", itemId, orderId);

        Order order = findOrder(orderId);

        checkModifiable(order);

        OrderItem item = order.getOrderItems().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(ORDER_ITEM_NOT_FOUND_MESSAGE));

        order.removeOrderItem(item);
        order.calculateTotalAmount();

        Order updated = orderRepository.save(order);

        return orderMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public OrderResponse updateStatus(Long orderId, UpdateOrderStatusRequest request) {
        log.info("update order {} status to {}", orderId, request.status());

        Order order = findOrder(orderId);

        applyStatus(order, request.status());

        Order updated = orderRepository.save(order);

        return orderMapper.toResponse(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long id) {
        return orderRepository.findOrderWithDetailsById(id)
                .map(orderMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException(ORDER_NOT_FOUND_MESSAGE));
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersBySession(Long sessionId) {
        return orderRepository.findByTableSessionId(sessionId).stream()
                .map(orderMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getActiveOrders() {
        return orderRepository.findByStatusIn(ACTIVE_STATUSES).stream()
                .map(orderMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getKitchenQueue() {
        return orderRepository.findByStatusIn(KITCHEN_STATUSES).stream()
                .sorted(Comparator.comparing(Order::getCreatedAt))
                .map(orderMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public OrderResponse cancelOrder(Long id) {
        log.info("cancel order: {}", id);

        Order order = findOrder(id);

        checkModifiable(order);

        order.cancel();

        Order updated = orderRepository.save(order);

        return orderMapper.toResponse(updated);
    }

    private Order findOrder(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ORDER_NOT_FOUND_MESSAGE));
    }

    private void checkModifiable(Order order) {
        if (List.of(OrderStatus.SERVED, OrderStatus.CLOSED, OrderStatus.CANCELLED, OrderStatus.COMPLETED)
                .contains(order.getStatus())) {
            throw new BusinessException(ORDER_NOT_MODIFIABLE_MESSAGE);
        }
    }

    private void addOrderItem(Order order, OrderItemRequest request) {
        Dish dish = dishRepository.findById(request.dishId())
                .orElseThrow(() -> new ResourceNotFoundException(DISH_NOT_FOUND_MESSAGE));

        if (!dish.isAvailable()) {
            throw new BusinessException(DISH_NOT_AVAILABLE_MESSAGE);
        }

        OrderItem item = OrderItem.of(dish, request.quantity());
        order.addOrderItem(item);
    }

    private void applyStatus(Order order, OrderStatus status) {
        if (List.of(OrderStatus.CLOSED, OrderStatus.CANCELLED).contains(order.getStatus())) {
            throw new BusinessException(ORDER_ALREADY_CLOSED_MESSAGE);
        }

        switch (status) {
            case IN_PROGRESS -> order.inProgress();
            case READY -> order.ready();
            case SERVED -> order.served();
            case COMPLETED -> order.complete();
            case CLOSED -> order.close();
            case CANCELLED -> order.cancel();
            default -> throw new BusinessException(INVALID_ORDER_STATUS_MESSAGE);
        }
    }
}
