package com.gabriel.springboot.app.menuflow.models.entities;

import com.gabriel.springboot.app.menuflow.exceptions.InvalidPriceException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(
        name = "dish",
        indexes = {
                @Index(name = "idx_available", columnList = "available"),
                @Index(name = "idx_category_id", columnList = "category_id")
        }
)
@Getter
@NoArgsConstructor
public class Dish extends BaseEntity{

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price = BigDecimal.ZERO;

    @Column(nullable = false)
    private boolean available = true;

    @Column(name = "image_path",length = 500)
    private String imagePath;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @Getter(AccessLevel.NONE)
    @OneToMany(
            mappedBy = "dish",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            orphanRemoval = true
    )
    private List<DishTranslation> translations = new ArrayList<>();

    @Getter(AccessLevel.NONE)
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "ingredient_dish",
            joinColumns = @JoinColumn(name = "dish_id"),
            inverseJoinColumns = @JoinColumn(name = "ingredient_id"),
            uniqueConstraints = {@UniqueConstraint(columnNames = {"ingredient_id", "dish_id"})}
    )
    private List<Ingredient> ingredients = new ArrayList<>();

    @Getter(AccessLevel.NONE)
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "dish_allergen",
            joinColumns = @JoinColumn(name = "dish_id"),
            inverseJoinColumns = @JoinColumn(name = "allergen_id"),
            uniqueConstraints = {@UniqueConstraint(columnNames = {"allergen_id", "dish_id"})}
    )
    private List<Allergen> allergens = new ArrayList<>();

    @Getter(AccessLevel.NONE)
    @OneToMany(
            mappedBy = "dish",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            orphanRemoval = true
    )
    private List<OrderItem> orderItems = new ArrayList<>();

    public static Dish of(Category category, BigDecimal price, User createdBy){
        Dish dish = new Dish();
        dish.assignCategory(category);
        dish.assignPrice(price);
        dish.assignCreatedBy(createdBy);
        dish.available();
        return dish;
    }

    public List<DishTranslation> getTranslations() {
        return Collections.unmodifiableList(translations);
    }

    public void addOrderItem(OrderItem orderItem){
        if (!this.orderItems.contains(orderItem)) {
            this.orderItems.add(orderItem);
            orderItem.assignDish(this);
        }
    }

    public void removeOrderItem(OrderItem orderItem){
        if (this.orderItems.remove(orderItem)) {
            orderItem.detachOrder();
        }
    }

    public void addAllergen(Allergen allergen){
        this.allergens.add(allergen);
        allergen.addDish(this);
    }

    public void removeAllergen(Allergen allergen){
        this.allergens.remove(allergen);
        allergen.removeDish(this);
    }

    public List<Allergen> getAllergens(){
        return Collections.unmodifiableList(allergens);
    }

    public void addIngredient(Ingredient ingredient){
        ingredients.add(ingredient);
        ingredient.addDish(this);
    }

    public void removeIngredient(Ingredient ingredient){
        ingredients.remove(ingredient);
        ingredient.removeDish(this);
    }

    public List<Ingredient> getIngredients(){
        return Collections.unmodifiableList(ingredients);
    }

    public void assignCategory(Category category){
        this.category = category;
        category.addDish(this);
    }

    public void detachCategory(Category category){
        this.category = null;
    }

    public void assignPrice(BigDecimal price){
        if(price == null || price.signum() < 0){
            throw new InvalidPriceException();
        }
        this.price = price;
    }

    public void available (){
        this.available = true;
    }

    public void unavailable (){
        this.available = false;
    }

    public void assignCreatedBy(User createdBy){
        this.createdBy = createdBy;
    }

    public void assignImageUrl(String imagePath){
        this.imagePath = imagePath;
    }

    public void addTranslation(String lang, String name, String description){
        DishTranslation translation = DishTranslation.of(this, lang, name, description);
        translations.add(translation);
    }

    public String getName(String lang){
        return translations.stream()
                .filter(t -> t.getLang().equals(lang))
                .map(DishTranslation::getName)
                .findFirst()
                .orElse(null);
    }

    public String getDescription(String lang){
        return translations.stream()
                .filter(t -> t.getLang().equals(lang))
                .map(DishTranslation::getDescription)
                .findFirst()
                .orElse(null);
    }

    public void updateTranslation(String lang, String name, String description){
        translations.removeIf(t -> t.getLang().equals(lang));
        addTranslation(lang, name, description);
    }
}