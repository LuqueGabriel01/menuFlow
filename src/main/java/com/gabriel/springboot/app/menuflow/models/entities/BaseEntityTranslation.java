package com.gabriel.springboot.app.menuflow.models.entities;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@MappedSuperclass
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class BaseEntityTranslation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 2, nullable = false)
    private String lang;

    @Column(length = 100, nullable = false)
    private String name;

    protected BaseEntityTranslation(String lang, String name) {
        this.lang = lang;
        this.name = name;
    }
}