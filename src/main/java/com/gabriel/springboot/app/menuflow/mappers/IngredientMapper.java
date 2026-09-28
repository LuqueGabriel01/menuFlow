package com.gabriel.springboot.app.menuflow.mappers;

import com.gabriel.springboot.app.menuflow.models.dto.response.product.IngredientResponse;
import com.gabriel.springboot.app.menuflow.models.entities.Ingredient;
import org.springframework.stereotype.Component;

@Component
public class IngredientMapper {
    public IngredientResponse toResponse(Ingredient ingredient, String lang) {
        return new IngredientResponse(
                ingredient.getId(),
                ingredient.getName(lang)
        );
    }
}
