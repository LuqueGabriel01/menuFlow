package com.gabriel.springboot.app.menuflow.models.entities;

import com.gabriel.springboot.app.menuflow.exceptions.InvalidInvoiceException;
import com.gabriel.springboot.app.menuflow.exceptions.InvalidPriceException;
import com.gabriel.springboot.app.menuflow.models.entities.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(
        name = "orders",
        indexes = {
                @Index(name = "idx_session_id", columnList = "session_id"),
                @Index(name = "idx_status", columnList = "status")
        }
)
@Getter
@NoArgsConstructor
public class Order extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private TableSession tableSession;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(name = "total_amount", precision = 10, scale = 2, nullable = false)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Column(name = "closed_at")
    private LocalDateTime closedAt;

    @Getter(AccessLevel.NONE)
    @OneToMany(
            mappedBy = "order",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            orphanRemoval = true
    )
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToOne(
            mappedBy = "order",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE}
    )
    private Invoice invoice;

    public static Order of(TableSession session){
        Order order = new Order();
        order.tableSession = session;
        order.status = OrderStatus.PENDING;
        order.totalAmount = BigDecimal.ZERO;

        session.addOrder(order);

        return order;
    }

    public void addOrderItem(OrderItem orderItem) {
        if (!this.orderItems.contains(orderItem)) {
            this.orderItems.add(orderItem);
            orderItem.assignOrder(this);
        }
    }

    public void removeOrderItem(OrderItem orderItem) {
        orderItems.remove(orderItem);
        orderItem.detachOrder();
    }

    public List<OrderItem> getOrderItems() {
        return Collections.unmodifiableList(orderItems);
    }

    public void assignInvoice(Invoice invoice) {
        if (invoice == null) throw new InvalidInvoiceException();
        if (this.invoice == invoice) return;

        this.invoice = invoice;
        invoice.addOrder(this);
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
        this.closedAt = LocalDateTime.now();
    }

    public void cancel(){
        this.status = OrderStatus.CANCELLED;
        this.closedAt = LocalDateTime.now();
    }

    public BigDecimal calculateTotalAmount(){
        return this.totalAmount = this.orderItems.stream()
                .map(OrderItem::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
