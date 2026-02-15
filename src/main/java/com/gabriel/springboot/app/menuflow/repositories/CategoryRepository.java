package com.gabriel.springboot.app.menuflow.repositories;

import com.gabriel.springboot.app.menuflow.models.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query("SELECT c FROM Category c JOIN FETCH c.translations t WHERE t.lang = :lang")
    List<Category> findAllByLanguage(@Param("lang") String lang);

    @Query("SELECT COUNT(t) > 0 FROM CategoryTranslation t WHERE t.name = :name AND t.lang = :lang")
    boolean existsByNameAndLang(@Param("name") String name, @Param("lang") String lang);

}
