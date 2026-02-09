package com.gabriel.springboot.app.menuflow.models.entities;

import com.gabriel.springboot.app.menuflow.models.entities.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(
        name = "order_item",
        indexes = {
                @Index(name = "idx_order_id", columnList = "order_id"),
                @Index(name = "idx_status", columnList = "status")
        }
)
@Getter
@NoArgsConstructor
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private BigDecimal price = BigDecimal.ZERO;

    @Column(nullable = false)
    private Integer quantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dish_id")
    private Dish dish;

    public static OrderItem of(Dish dish, Integer quantity){
        OrderItem orderItem = new OrderItem();
        orderItem.dish = dish;
        orderItem.quantity = quantity;
        orderItem.calculatePrice(dish, quantity);
        orderItem.pending();
        return orderItem;
    }

    public void calculatePrice(Dish dish, Integer quantity){
        this.price = dish.getPrice().multiply(BigDecimal.valueOf(quantity));
    }

    public void assignOrder(Order order) {
        this.order = order;
        order.addOrderItem(this);
    }

    public void detachOrder() {
        this.order = null;
    }

    public void assignDish(Dish dish) {
        this.dish = dish;
        dish.addOrderItem(this);
    }

    public void detachDish() {
        this.dish = null;
    }

    public void inProgress() {
        this.status = OrderStatus.IN_PROGRESS;
    }

    public void complete() {
        this.status = OrderStatus.COMPLETED;
    }

    public void ready() {
        this.status = OrderStatus.READY;
    }

    public void served() {
        this.status = OrderStatus.SERVED;
    }

    public void close(){
        this.status = OrderStatus.CLOSED;
    }

    public void cancel(){
        this.status = OrderStatus.CANCELLED;
    }

    public void pending(){
        this.status = OrderStatus.PENDING;
    }
}
