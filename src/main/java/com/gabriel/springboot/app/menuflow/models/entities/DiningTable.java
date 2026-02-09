package com.gabriel.springboot.app.menuflow.models.entities;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "dining_tables",
        uniqueConstraints = {
                @UniqueConstraint(name = "number", columnNames = "number"),
                @UniqueConstraint(name = "qr_code", columnNames = "qr_code")
        }
)
@Getter
@NoArgsConstructor
public class DiningTable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private Integer number;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    @Column(name = "qr_code", unique = true, nullable = false)
    private String qrCode;

    @Getter(AccessLevel.NONE)
    @OneToMany(
            mappedBy = "diningTable",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<TableSession> sessions = new ArrayList<>();

    public static DiningTable of(Integer number, String qrCode){
        DiningTable table = new DiningTable();
        table.number = number;
        table.qrCode = qrCode;
        table.isActive = true;
        return table;
    }

    public void disable(){
        this.isActive = false;
    }

    public void enable(){
        this.isActive = true;
    }

    public void addSession(TableSession session){
        this.sessions.add(session);
        session.assignTable(DiningTable.this);
    }
}
