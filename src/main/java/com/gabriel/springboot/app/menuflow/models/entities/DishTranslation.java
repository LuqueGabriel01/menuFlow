package com.gabriel.springboot.app.menuflow.models.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "dish_translation",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"dish_id","lang"}),
                @UniqueConstraint(columnNames = {"name", "lang"})
        }
)
@Getter
@NoArgsConstructor
public class DishTranslation extends BaseEntityTranslation{

    @Column(nullable = false)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dish_id")
    private Dish dish;

    protected DishTranslation(Dish dish, String lang, String name, String description) {
        super(name, lang);
        this.dish = dish;
        this.description = description;
    }

    public static DishTranslation of(Dish dish, String lang, String name, String description) {
        return new DishTranslation(dish, lang, name, description);
    }
}
