package com.gabriel.springboot.app.menuflow.models.entities;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "allergen")
@Getter
@NoArgsConstructor
public class Allergen {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter(AccessLevel.NONE)
    @OneToMany(
            mappedBy = "allergen",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            orphanRemoval = true
    )
    private List<AllergenTranslation> translations = new ArrayList<>();

    @Getter(AccessLevel.NONE)
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "dish_allergen",
            joinColumns = @JoinColumn(name = "allergen_id"),
            inverseJoinColumns = @JoinColumn(name = "dish_id"),
            uniqueConstraints = {@UniqueConstraint(columnNames = {"allergen_id","dish_id"})}
    )
    private List<Dish> dishes = new ArrayList<>();

    public static Allergen of(){
        return new Allergen();
    }

    public List<Dish> getDishes() {
        return Collections.unmodifiableList(dishes);
    }

    public void addDish(Dish dish){
        if (!this.dishes.contains(dish)) {
            this.dishes.add(dish);
            dish.addAllergen(this);
        }
    }

    public void removeDish(Dish dish){
        if (this.dishes.contains(dish)) {
            this.dishes.remove(dish);
            dish.removeAllergen(this);
        }
    }

    public void addTranslation(String lang, String name){
        AllergenTranslation translation = AllergenTranslation.of(this, lang, name);
        translations.add(translation);
    }

    public List<AllergenTranslation> getTranslations() {
        return Collections.unmodifiableList(translations);
    }
}
