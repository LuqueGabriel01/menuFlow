package com.gabriel.springboot.app.menuflow.mappers;

import com.gabriel.springboot.app.menuflow.models.dto.response.product.DishResponse;
import com.gabriel.springboot.app.menuflow.models.entities.Dish;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DishMapper {

    private final IngredientMapper ingredientMapper;
    private final AllergenMapper allergenMapper;

    public DishResponse toResponse(Dish dish, String lang) {
        return new DishResponse(
                dish.getId(),
                dish.getCategory().getId(),
                dish.getCategory().getName(lang),
                dish.getName(lang),
                dish.getDescription(lang),
                dish.getPrice(),
                dish.isAvailable(),
                dish.getImagePath(),
                dish.getImagePath(),
                dish.getCreatedAt(),
                dish.getCreatedBy() != null ? dish.getCreatedBy().getUsername() : null,
                dish.getIngredients().stream().map(i -> ingredientMapper.toResponse(i, lang)).toList(),
                dish.getAllergens().stream().map(a -> allergenMapper.toResponse(a, lang)).toList()
        );
    }
}
