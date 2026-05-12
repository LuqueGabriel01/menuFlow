package com.gabriel.springboot.app.menuflow.models.dto.response.auth;

import lombok.Builder;

import java.util.List;

import static com.gabriel.springboot.app.menuflow.constants.SecurityConstants.TOKEN_TYPE;

@Builder
public record AuthResponse(
        String token,
        String type,
        String username,
        List<String> roles
) {
    public AuthResponse{
        if(type == null){
            type = TOKEN_TYPE;
        }
    }
}
