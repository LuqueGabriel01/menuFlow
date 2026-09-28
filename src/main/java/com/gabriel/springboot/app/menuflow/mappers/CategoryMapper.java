package com.gabriel.springboot.app.menuflow.mappers;

import com.gabriel.springboot.app.menuflow.models.dto.response.product.CategoryResponse;
import com.gabriel.springboot.app.menuflow.models.entities.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {
    public CategoryResponse toResponse(Category category, String lang) {
        return new CategoryResponse(
                category.getId(),
                category.getName(lang),
                category.getDishes().size()
        );
    }
}
