package com.gabriel.springboot.app.menuflow.services;

import com.gabriel.springboot.app.menuflow.models.dto.request.product.CreateCategoryRequest;
import com.gabriel.springboot.app.menuflow.models.dto.request.product.UpdateCategoryRequest;
import com.gabriel.springboot.app.menuflow.models.dto.response.product.CategoryResponse;

import java.util.List;

public interface CategoryService {
    CategoryResponse createCategory(CreateCategoryRequest request);
    CategoryResponse updateCategory(Long id, UpdateCategoryRequest request);
    CategoryResponse getCategoryById(Long id);
    List<CategoryResponse> getAllCategories();
    void deleteCategoryById(Long id);
}
