package com.gabriel.springboot.app.menuflow.models.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "category_translation",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"category_id", "lang"}),
                @UniqueConstraint(columnNames = {"name","lang"})
        }
)
@Getter
@NoArgsConstructor
public class CategoryTranslation extends BaseEntityTranslation {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    protected CategoryTranslation(Category category, String lang, String name) {
        super(lang, name);
        this.category = category;
    }

    public static CategoryTranslation of(Category category, String lang, String name) {
        return new CategoryTranslation(category, lang, name);
    }
}
