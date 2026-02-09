package com.gabriel.springboot.app.menuflow.models.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "ingredient_translation",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"ingredient_id","lang"}),
                @UniqueConstraint(columnNames = {"name","lang"})
        }
)
@Getter
@NoArgsConstructor
public class IngredientTranslation extends BaseEntityTranslation{

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingredient_id", nullable = false)
    private Ingredient ingredient;

    protected IngredientTranslation(Ingredient ingredient, String lang, String name) {
        super(lang, name);
        this.ingredient = ingredient;
    }

    public static IngredientTranslation of(Ingredient ingredient, String lang, String name) {
        return new IngredientTranslation(ingredient, lang, name);
    }
}
