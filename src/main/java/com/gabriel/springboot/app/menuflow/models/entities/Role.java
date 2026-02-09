package com.gabriel.springboot.app.menuflow.models.entities;

import com.gabriel.springboot.app.menuflow.models.entities.enums.RoleName;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Table(
        name = "roles",
        uniqueConstraints = {@UniqueConstraint(name = "name", columnNames = "name")}
)
public class Role extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(unique = true, nullable = false)
    private RoleName name;

    protected Role(RoleName name) {
        this.name = name;
    }

    public static Role of(RoleName name) {
        Role role = new Role();
        role.name = name;
        return role;
    }

}
