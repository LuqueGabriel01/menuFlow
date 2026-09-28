package com.gabriel.springboot.app.menuflow.mappers;

import com.gabriel.springboot.app.menuflow.models.dto.response.product.AllergenResponse;
import com.gabriel.springboot.app.menuflow.models.entities.Allergen;
import org.springframework.stereotype.Component;

@Component
public class AllergenMapper {
    public AllergenResponse toResponse(Allergen allergen, String lang) {
        return new AllergenResponse(
                allergen.getId(),
                allergen.getName(lang)
        );
    }
}
