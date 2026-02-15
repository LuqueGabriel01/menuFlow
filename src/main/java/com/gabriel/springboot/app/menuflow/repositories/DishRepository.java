package com.gabriel.springboot.app.menuflow.repositories;

import com.gabriel.springboot.app.menuflow.models.entities.Dish;
import com.gabriel.springboot.app.menuflow.models.entities.DishTranslation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DishRepository extends JpaRepository<Dish, Long> {

    List<Dish> findByCategoryId(Long categoryId);

    List<Dish> findByAvailableTrue();

    List<Dish> findByCategoryIdAndAvailableTrue(Long categoryId);

    @Query("SELECT d FROM Dish d JOIN FETCH d.translations t WHERE LOWER(t.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Dish> findByName(@Param("name") String name);

    @Query("SELECT d FROM Dish d JOIN FETCH d.translations t WHERE t.lang = :lang")
    List<Dish> findAllByLanguage(@Param("lang") String lang);
}
