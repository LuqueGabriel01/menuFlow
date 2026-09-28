package com.gabriel.springboot.app.menuflow.models.entities;

import com.gabriel.springboot.app.menuflow.models.entities.enums.PaymentMethod;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "invoice",
        uniqueConstraints = {
                @UniqueConstraint(name = "order_id", columnNames = "order_id")
        }
)
@Getter
@NoArgsConstructor
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "total_amount", nullable = false)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Column(name = "paid_at", nullable = false)
    private LocalDateTime paidAt = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    private PaymentMethod paymentMethod;

    public static Invoice of(Order order) {
        Invoice invoice = new Invoice();

        invoice.order = order;
        invoice.totalAmount = order.calculateTotalAmount();
        invoice.paidAt = LocalDateTime.now();

        return invoice;
    }

    public void addOrder(Order order) {
        if (this.order == order) return;
        this.order = order;
        order.assignInvoice(this);
    }

    public BigDecimal calculateTotalAmount(Order order) {
        return this.totalAmount = order.getTotalAmount();
    }

    public void card(){
        this.paymentMethod = PaymentMethod.CARD;
        this.order.close();
    }

    public void transfer(){
        this.paymentMethod = PaymentMethod.TRANSFER;
        this.order.close();
    }

    public void cash(){
        this.paymentMethod = PaymentMethod.CASH;
        this.order.close();
    }

    public void assignUser(User user) {
        this.createdBy = user;
    }

}
