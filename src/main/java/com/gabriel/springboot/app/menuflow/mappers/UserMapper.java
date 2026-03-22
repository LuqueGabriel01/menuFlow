package com.gabriel.springboot.app.menuflow.mappers;

import com.gabriel.springboot.app.menuflow.models.dto.request.RegisterRequest;
import com.gabriel.springboot.app.menuflow.models.entities.Role;
import com.gabriel.springboot.app.menuflow.models.entities.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public User toEntity(RegisterRequest request, String encodedPassword, Role role) {
        User user = User.of(request.username(), request.email());
        user.updatePassword(encodedPassword);
        user.addRole(role);
        return user;
    }
}
