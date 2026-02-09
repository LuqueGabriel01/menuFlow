package com.gabriel.springboot.app.menuflow.models.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "allergen_translation",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"allergen_id","lang"}),
                @UniqueConstraint(columnNames = {"name","lang"})
        }
)
@Getter
@NoArgsConstructor
public class AllergenTranslation extends BaseEntityTranslation{

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "allergen_id")
    private Allergen allergen;

    protected AllergenTranslation(Allergen allergen, String lang, String name) {
        super(name, lang);
        this.allergen = allergen;
    }

    public static AllergenTranslation of(Allergen allergen, String lang, String name) {
        return new AllergenTranslation(allergen, lang, name);
    }
}
