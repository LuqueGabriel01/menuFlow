package com.gabriel.springboot.app.menuflow.models.entities;

import com.gabriel.springboot.app.menuflow.exceptions.UserDisabledException;
import com.gabriel.springboot.app.menuflow.exceptions.UserEnabledException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(name = "email", columnNames = "email"),
                @UniqueConstraint(name = "username", columnNames = "username")
        }
)
@Getter
@NoArgsConstructor
public class User extends BaseEntity{

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private boolean enabled;

    @Getter(AccessLevel.NONE)
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"),
            uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "role_id"})}
    )
    private Set<Role> roles = new HashSet<>();

    public static User of(String username, String email){
        User user = new User();
        user.username = username;
        user.email = email;
        user.enabled = true;
        return user;
    }

    public void updatePassword(String encodedPassword) {
        this.password = encodedPassword;
    }

    public void enable(){
        if(enabled){
            throw new UserEnabledException();
        }
        this.enabled = true;
    }

    public void disable() {
        if (!enabled) {
            throw new UserDisabledException();
        }
        this.enabled = false;
    }

    public void addRole(Role role) {
        roles.add(role);
    }

    public void removeRole(Role role) {
        this.roles.remove(role);
    }
}
