package com.gabriel.springboot.app.menuflow.models.dto.request.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateAllergenRequest(
        @NotBlank
        @Size(min = 3, max = 100)
        String name
) {
}
