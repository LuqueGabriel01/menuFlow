package com.gabriel.springboot.app.menuflow.models.entities;

import com.gabriel.springboot.app.menuflow.models.entities.enums.RoleName;
import com.gabriel.springboot.app.menuflow.models.entities.enums.SessionStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Table(
        name = "table_session",
        indexes = {
                @Index(name = "idx_table_id", columnList = "table_id"),
                @Index(name = "idx_status", columnList = "status")
        }
)
public class TableSession extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "table_id", nullable = false)
    private DiningTable diningTable;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SessionStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "opened_by")
    private User openedBy;

    @Column(name = "closed_at")
    private LocalDateTime closedAt;

    @Getter(AccessLevel.NONE)
    @OneToMany(
            mappedBy = "tableSession",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Order> orders =  new ArrayList<>();

    public static TableSession of(DiningTable diningTable) {
        TableSession session = new TableSession();
        session.diningTable = diningTable;
        session.status = SessionStatus.OPEN;

        return session;
    }

    public void close() {
        this.status = SessionStatus.CLOSED;
        this.closedAt = LocalDateTime.now();
    }

    public void open() {
        this.status = SessionStatus.OPEN;
    }

    public void addOrder(Order order) {
        if (this.orders == null) return;
        this.orders.add(order);
    }

    public void removeOrder(Order order) {
        if (this.orders == null) return;
        this.orders.remove(order);
    }

    public void openedBy(User openedBy) {
        this.openedBy = openedBy;
    }

    public void assignTable(DiningTable diningTable) {
        this.diningTable = diningTable;
    }
}
