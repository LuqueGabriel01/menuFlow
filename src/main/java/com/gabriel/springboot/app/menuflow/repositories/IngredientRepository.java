package com.gabriel.springboot.app.menuflow.repositories;

import com.gabriel.springboot.app.menuflow.models.entities.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IngredientRepository extends JpaRepository<Ingredient, Long> {

    @Query("SELECT i FROM Ingredient i JOIN FETCH i.translations t WHERE t.lang = :lang")
    List<Ingredient> findAllByLanguage(@Param("lang") String lang);

    @Query("SELECT COUNT(t) > 0 FROM IngredientTranslation t WHERE t.name = :name AND t.lang = :lang")
    boolean existsNameAndLang(@Param("name") String name, @Param("lang") String lang);
}
