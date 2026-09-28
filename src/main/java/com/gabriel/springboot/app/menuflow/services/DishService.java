package com.gabriel.springboot.app.menuflow.services;

import com.gabriel.springboot.app.menuflow.models.dto.request.product.CreateDishRequest;
import com.gabriel.springboot.app.menuflow.models.dto.request.product.UpdateDishRequest;
import com.gabriel.springboot.app.menuflow.models.dto.response.product.DishResponse;

import java.util.List;

public interface DishService {
    DishResponse createDish(CreateDishRequest request);
    DishResponse updateDish(Long id, UpdateDishRequest request);
    DishResponse getDishById(Long id);
    List<DishResponse> getAllDishes();
    List<DishResponse> getAvailableDishes();
    List<DishResponse> getDishesByCategory(Long categoryId);
    void deleteDishById(Long id);
}
