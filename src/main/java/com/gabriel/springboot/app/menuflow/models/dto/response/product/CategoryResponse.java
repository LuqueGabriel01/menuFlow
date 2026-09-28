package com.gabriel.springboot.app.menuflow.models.dto.response.product;

public record CategoryResponse(
        Long id,
        String name,
        Integer dishCount
) {
}
