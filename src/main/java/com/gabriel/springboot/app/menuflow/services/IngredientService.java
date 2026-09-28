package com.gabriel.springboot.app.menuflow.services;

import com.gabriel.springboot.app.menuflow.models.dto.request.product.CreateIngredientRequest;
import com.gabriel.springboot.app.menuflow.models.dto.request.product.UpdateIngredientRequest;
import com.gabriel.springboot.app.menuflow.models.dto.response.product.IngredientResponse;

import java.util.List;

public interface IngredientService {
    IngredientResponse createIngredient(CreateIngredientRequest request);
    IngredientResponse updateIngredient(Long id, UpdateIngredientRequest request);
    IngredientResponse getIngredientById(Long id);
    List<IngredientResponse> getAllIngredients();
    void deleteIngredientById(Long id);
}
