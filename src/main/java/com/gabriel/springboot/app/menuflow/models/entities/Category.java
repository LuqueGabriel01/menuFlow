package com.gabriel.springboot.app.menuflow.models.entities;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "category")
@NoArgsConstructor
@Getter
public class Category{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter(AccessLevel.NONE)
    @OneToMany(
            mappedBy = "category",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            orphanRemoval = true
    )
    private List<CategoryTranslation> translations = new ArrayList<>();

    @Getter(AccessLevel.NONE)
    @OneToMany(
            mappedBy = "category",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            orphanRemoval = true
    )
    private List<Dish> dishes = new ArrayList<>();

    public static Category of(){
        return new Category();
    }

    public void addTranslation(String lang, String name){
        CategoryTranslation translation = CategoryTranslation.of(this, lang, name);
        translations.add(translation);
    }

    public void addDish(Dish dish){
        if(!this.dishes.contains(dish)){
            this.dishes.add(dish);
            dish.assignCategory(this);
        }
    }

    public List<Dish> getDishes(){
        return Collections.unmodifiableList(this.dishes);
    }

    public List<CategoryTranslation> getTranslations(){
        return Collections.unmodifiableList(this.translations);
    }

    public String getName(String lang){
        return translations.stream()
                .filter(t -> t.getLang().equals(lang))
                .map(CategoryTranslation::getName)
                .findFirst()
                .orElse(null);
    }

    public void updateTranslation(String lang, String name){
        translations.removeIf(t -> t.getLang().equals(lang));
        addTranslation(lang, name);
    }
}
