package com.gabriel.springboot.app.menuflow.models.dto.response.product;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record DishResponse(
        Long id,
        Long categoryId,
        String categoryName,
        String name,
        String description,
        BigDecimal price,
        Boolean available,
        String imagePath,
        String imageUrl,
        LocalDateTime createdAt,
        String createdBy,
        List<IngredientResponse> ingredients,
        List<AllergenResponse> allergens
) {
}
