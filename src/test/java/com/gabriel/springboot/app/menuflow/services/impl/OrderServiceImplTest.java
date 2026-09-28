package com.gabriel.springboot.app.menuflow.services.impl;

import com.gabriel.springboot.app.menuflow.exceptions.BusinessException;
import com.gabriel.springboot.app.menuflow.exceptions.ResourceNotFoundException;
import com.gabriel.springboot.app.menuflow.mappers.OrderMapper;
import com.gabriel.springboot.app.menuflow.models.dto.request.order.CreateOrderRequest;
import com.gabriel.springboot.app.menuflow.models.dto.request.order.OrderItemRequest;
import com.gabriel.springboot.app.menuflow.models.dto.request.order.UpdateOrderStatusRequest;
import com.gabriel.springboot.app.menuflow.models.dto.response.order.OrderResponse;
import java.util.Collections;
import com.gabriel.springboot.app.menuflow.models.entities.Category;
import com.gabriel.springboot.app.menuflow.models.entities.DiningTable;
import com.gabriel.springboot.app.menuflow.models.entities.Dish;
import com.gabriel.springboot.app.menuflow.models.entities.Order;
import com.gabriel.springboot.app.menuflow.models.entities.OrderItem;
import com.gabriel.springboot.app.menuflow.models.entities.TableSession;
import com.gabriel.springboot.app.menuflow.models.entities.enums.OrderStatus;
import com.gabriel.springboot.app.menuflow.models.entities.enums.SessionStatus;
import com.gabriel.springboot.app.menuflow.repositories.DishRepository;
import com.gabriel.springboot.app.menuflow.repositories.OrderRepository;
import com.gabriel.springboot.app.menuflow.repositories.TableSessionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private TableSessionRepository sessionRepository;

    @Mock
    private DishRepository dishRepository;

    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private OrderServiceImpl orderService;

    private DiningTable table() {
        return DiningTable.of(5, "qr-123");
    }

    private TableSession openSession() {
        return TableSession.of(table());
    }

    private Dish availableDish() {
        Category category = Category.of();
        Dish dish = Dish.of(category, BigDecimal.TEN, null);
        dish.available();
        return dish;
    }

    @Test
    @DisplayName("Should create order successfully when session is open and dish is available")
    void createOrder() {
        CreateOrderRequest request = new CreateOrderRequest(1L, List.of(new OrderItemRequest(1L, 2)));

        TableSession session = openSession();
        Dish dish = availableDish();
        Order savedOrder = Order.of(session);
        OrderResponse response = OrderResponse.builder()
                .id(1L)
                .status(OrderStatus.PENDING)
                .totalAmount(BigDecimal.ZERO)
                .items(Collections.emptyList())
                .build();

        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
        when(dishRepository.findById(1L)).thenReturn(Optional.of(dish));
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
        when(orderMapper.toResponse(savedOrder)).thenReturn(response);

        OrderResponse result = orderService.createOrder(request);

        assertNotNull(result);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    @DisplayName("Should throw BusinessException when session is not open")
    void createOrderSessionClosed() {
        CreateOrderRequest request = new CreateOrderRequest(1L, List.of(new OrderItemRequest(1L, 2)));

        TableSession session = openSession();
        session.close();

        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));

        assertThrows(BusinessException.class, () -> orderService.createOrder(request));

        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("Should throw BusinessException when dish is not available")
    void createOrderDishNotAvailable() {
        CreateOrderRequest request = new CreateOrderRequest(1L, List.of(new OrderItemRequest(1L, 2)));

        TableSession session = openSession();
        Category category = Category.of();
        Dish dish = Dish.of(category, BigDecimal.TEN, null);
        dish.unavailable();

        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
        when(dishRepository.findById(1L)).thenReturn(Optional.of(dish));

        assertThrows(BusinessException.class, () -> orderService.createOrder(request));

        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when order does not exist")
    void getOrderByIdNotFound() {
        when(orderRepository.findOrderWithDetailsById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> orderService.getOrderById(1L));
    }

    @Test
    @DisplayName("Should add item to a modifiable order")
    void addItem() {
        TableSession session = openSession();
        Order order = Order.of(session);
        Dish dish = availableDish();
        OrderResponse response = OrderResponse.builder()
                .id(1L)
                .status(OrderStatus.PENDING)
                .totalAmount(BigDecimal.ZERO)
                .items(Collections.emptyList())
                .build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(dishRepository.findById(2L)).thenReturn(Optional.of(dish));
        when(orderRepository.save(order)).thenReturn(order);
        when(orderMapper.toResponse(order)).thenReturn(response);

        OrderResponse result = orderService.addItem(1L, new OrderItemRequest(2L, 1));

        assertNotNull(result);
        assertEquals(1, order.getOrderItems().size());
    }

    @Test
    @DisplayName("Should throw BusinessException when adding an item to a served order")
    void addItemNotModifiable() {
        TableSession session = openSession();
        Order order = Order.of(session);
        order.inProgress();
        order.ready();
        order.served();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThrows(BusinessException.class, () -> orderService.addItem(1L, new OrderItemRequest(2L, 1)));
    }

    @Test
    @DisplayName("Should remove an existing item from an order")
    void removeItem() {
        TableSession session = openSession();
        Order order = Order.of(session);
        Dish dish = availableDish();
        OrderItem item = OrderItem.of(dish, 1);
        setId(item, OrderItem.class, 1L);
        order.addOrderItem(item);

        OrderResponse response = OrderResponse.builder()
                .id(1L)
                .status(OrderStatus.PENDING)
                .totalAmount(BigDecimal.ZERO)
                .items(Collections.emptyList())
                .build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);
        when(orderMapper.toResponse(order)).thenReturn(response);

        OrderResponse result = orderService.removeItem(1L, item.getId());

        assertNotNull(result);
        assertTrue(order.getOrderItems().isEmpty());
    }

    @Test
    @DisplayName("Should update order status through the kitchen workflow")
    void updateStatus() {
        TableSession session = openSession();
        Order order = Order.of(session);
        OrderResponse response = OrderResponse.builder()
                .id(1L)
                .status(OrderStatus.PENDING)
                .totalAmount(BigDecimal.ZERO)
                .items(Collections.emptyList())
                .build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);
        when(orderMapper.toResponse(order)).thenReturn(response);

        OrderResponse result = orderService.updateStatus(1L, new UpdateOrderStatusRequest(OrderStatus.IN_PROGRESS));

        assertNotNull(result);
        assertEquals(OrderStatus.IN_PROGRESS, order.getStatus());
    }

    @Test
    @DisplayName("Should throw BusinessException when updating status of a closed order")
    void updateStatusAlreadyClosed() {
        TableSession session = openSession();
        Order order = Order.of(session);
        order.inProgress();
        order.ready();
        order.served();
        order.close();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThrows(BusinessException.class,
                () -> orderService.updateStatus(1L, new UpdateOrderStatusRequest(OrderStatus.READY)));
    }

    @Test
    @DisplayName("Should return list of active orders")
    void getActiveOrders() {
        when(orderRepository.findByStatusIn(anyList())).thenReturn(List.of());

        List<OrderResponse> result = orderService.getActiveOrders();

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should return kitchen queue sorted from oldest to newest")
    void getKitchenQueue() {
        TableSession session = openSession();

        Order older = Order.of(session);
        Order newer = Order.of(session);

        setCreatedAt(older, java.time.LocalDateTime.now().minusMinutes(10));
        setCreatedAt(newer, java.time.LocalDateTime.now());

        OrderResponse olderResponse = OrderResponse.builder().id(1L).status(OrderStatus.PENDING).build();
        OrderResponse newerResponse = OrderResponse.builder().id(2L).status(OrderStatus.IN_PROGRESS).build();

        when(orderRepository.findByStatusIn(List.of(OrderStatus.PENDING, OrderStatus.IN_PROGRESS)))
                .thenReturn(List.of(newer, older));
        when(orderMapper.toResponse(older)).thenReturn(olderResponse);
        when(orderMapper.toResponse(newer)).thenReturn(newerResponse);

        List<OrderResponse> result = orderService.getKitchenQueue();

        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).id());
        assertEquals(2L, result.get(1).id());
    }

    private void setId(Object entity, Class<?> declaringClass, Long id) {
        try {
            java.lang.reflect.Field field = declaringClass.getDeclaredField("id");
            field.setAccessible(true);
            field.set(entity, id);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    private void setCreatedAt(Order order, java.time.LocalDateTime value) {
        try {
            java.lang.reflect.Field field = com.gabriel.springboot.app.menuflow.models.entities.BaseEntity.class
                    .getDeclaredField("createdAt");
            field.setAccessible(true);
            field.set(order, value);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("Should cancel a modifiable order")
    void cancelOrder() {
        TableSession session = openSession();
        Order order = Order.of(session);
        OrderResponse response = OrderResponse.builder()
                .id(1L)
                .status(OrderStatus.PENDING)
                .totalAmount(BigDecimal.ZERO)
                .items(Collections.emptyList())
                .build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);
        when(orderMapper.toResponse(order)).thenReturn(response);

        OrderResponse result = orderService.cancelOrder(1L);

        assertNotNull(result);
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
    }
}
