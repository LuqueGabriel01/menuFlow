package com.gabriel.springboot.app.menuflow.models.dto.request.product;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.List;

public record UpdateDishRequest(

        @NotNull
        Long categoryId,

        @NotBlank
        @Size(min = 3, max = 100)
        String name,

        @NotBlank
        @Size(min = 10, max = 500)
        String description,

        @NotNull
        @DecimalMin("0.01")
        BigDecimal price,

        @NotNull
        Boolean available,

        List<Long> ingredientsIds,

        List<Long> allergensIds
) {
}
