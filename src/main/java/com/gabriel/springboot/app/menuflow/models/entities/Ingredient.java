package com.gabriel.springboot.app.menuflow.models.entities;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "ingredient")
@Getter
@NoArgsConstructor
public class Ingredient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter(AccessLevel.NONE)
    @OneToMany(
            mappedBy = "ingredient",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            orphanRemoval = true
    )
    List<IngredientTranslation> translations = new ArrayList<>();

    @Getter(AccessLevel.NONE)
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "ingredient_dish",
            joinColumns = @JoinColumn(name = "ingredient_id"),
            inverseJoinColumns = @JoinColumn(name = "dish_id"),
            uniqueConstraints = {@UniqueConstraint(columnNames = {"ingredient_id","dish_id"})}
    )
    private List<Dish> dishes = new ArrayList<>();

    public static Ingredient of() {
        return new Ingredient();
    }

    public List<Dish> getDishes() {
        return Collections.unmodifiableList(dishes);
    }

    public void addDish(Dish dish){
        this.dishes.add(dish);
        dish.addIngredient(this);
    }

    public void removeDish(Dish dish){
        this.dishes.remove(dish);
        dish.removeIngredient(this);
    }

    public void addTranslation(String lang, String name){
        IngredientTranslation translation = IngredientTranslation.of(this, lang, name);
        translations.add(translation);
    }

    public List<IngredientTranslation> getTranslations() {
        return Collections.unmodifiableList(translations);
    }

    public String getName(String lang){
        return translations.stream()
                .filter(t -> t.getLang().equals(lang))
                .map(IngredientTranslation::getName)
                .findFirst()
                .orElse(null);
    }

    public void updateTranslation(String lang, String name){
        translations.removeIf(t -> t.getLang().equals(lang));
        addTranslation(lang, name);
    }
}
