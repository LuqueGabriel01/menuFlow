package com.gabriel.springboot.app.menuflow.services.impl;

import com.gabriel.springboot.app.menuflow.exceptions.BusinessException;
import com.gabriel.springboot.app.menuflow.exceptions.ResourceNotFoundException;
import com.gabriel.springboot.app.menuflow.mappers.CategoryMapper;
import com.gabriel.springboot.app.menuflow.models.dto.request.product.CreateCategoryRequest;
import com.gabriel.springboot.app.menuflow.models.dto.request.product.UpdateCategoryRequest;
import com.gabriel.springboot.app.menuflow.models.dto.response.product.CategoryResponse;
import com.gabriel.springboot.app.menuflow.models.entities.Category;
import com.gabriel.springboot.app.menuflow.models.entities.Dish;
import com.gabriel.springboot.app.menuflow.repositories.CategoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static com.gabriel.springboot.app.menuflow.constants.LocaleConstants.DEFAULT_LANG;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Test
    @DisplayName("Should create category successfully when name does not exist")
    void createCategory() {
        CreateCategoryRequest request = new CreateCategoryRequest("Starters");

        Category category = Category.of();
        category.addTranslation(DEFAULT_LANG, "Starters");
        CategoryResponse response = new CategoryResponse(1L, "Starters", 0);

        when(categoryRepository.existsByNameAndLang("Starters", DEFAULT_LANG)).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenReturn(category);
        when(categoryMapper.toResponse(category, DEFAULT_LANG)).thenReturn(response);

        CategoryResponse result = categoryService.createCategory(request);

        assertNotNull(result);
        assertEquals("Starters", result.name());
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    @DisplayName("Should throw BusinessException when creating category with existing name")
    void createCategoryAlreadyExists() {
        CreateCategoryRequest request = new CreateCategoryRequest("Starters");

        when(categoryRepository.existsByNameAndLang("Starters", DEFAULT_LANG)).thenReturn(true);

        assertThrows(BusinessException.class, () -> categoryService.createCategory(request));

        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    @DisplayName("Should return category details when a valid ID is provided")
    void getCategoryById() {
        Category category = Category.of();
        category.addTranslation(DEFAULT_LANG, "Starters");
        CategoryResponse response = new CategoryResponse(1L, "Starters", 0);

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryMapper.toResponse(category, DEFAULT_LANG)).thenReturn(response);

        CategoryResponse result = categoryService.getCategoryById(1L);

        assertEquals("Starters", result.name());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when category does not exist")
    void getCategoryByIdNotFound() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> categoryService.getCategoryById(1L));
    }

    @Test
    @DisplayName("Should update category name when new name is available")
    void updateCategory() {
        UpdateCategoryRequest request = new UpdateCategoryRequest("Mains");

        Category category = Category.of();
        category.addTranslation(DEFAULT_LANG, "Starters");
        CategoryResponse response = new CategoryResponse(1L, "Mains", 0);

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryRepository.existsByNameAndLang("Mains", DEFAULT_LANG)).thenReturn(false);
        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.toResponse(category, DEFAULT_LANG)).thenReturn(response);

        CategoryResponse result = categoryService.updateCategory(1L, request);

        assertEquals("Mains", result.name());
    }

    @Test
    @DisplayName("Should return list of all categories")
    void getAllCategories() {
        when(categoryRepository.findAllByLanguage(DEFAULT_LANG)).thenReturn(List.of());

        List<CategoryResponse> result = categoryService.getAllCategories();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should delete category successfully when it has no dishes")
    void deleteCategory() {
        Category category = Category.of();

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        categoryService.deleteCategoryById(1L);

        verify(categoryRepository).delete(category);
    }

    @Test
    @DisplayName("Should throw BusinessException when deleting category with associated dishes")
    void deleteCategoryWithDishes() {
        Category category = Category.of();
        Dish dish = mock(Dish.class);
        category.addDish(dish);

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        assertThrows(BusinessException.class, () -> categoryService.deleteCategoryById(1L));

        verify(categoryRepository, never()).delete(any());
    }
}
