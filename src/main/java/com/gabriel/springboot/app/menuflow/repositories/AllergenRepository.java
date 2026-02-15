package com.gabriel.springboot.app.menuflow.repositories;

import com.gabriel.springboot.app.menuflow.models.entities.Allergen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AllergenRepository extends JpaRepository<Allergen, Long> {

    @Query("SELECT a FROM Allergen a JOIN FETCH a.translations t WHERE t.lang = :lang")
    List<Allergen> findAllByLanguage(@Param("lang") String lang);

    @Query("SELECT COUNT(t) > 0 FROM AllergenTranslation t WHERE t.name = :name AND t.lang = :lang")
    boolean existsByNameAndLang(@Param("name") String lang, @Param("lang") String name);
}
