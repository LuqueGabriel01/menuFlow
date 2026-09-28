package com.gabriel.springboot.app.menuflow.services.impl;

import com.gabriel.springboot.app.menuflow.exceptions.BusinessException;
import com.gabriel.springboot.app.menuflow.exceptions.ResourceNotFoundException;
import com.gabriel.springboot.app.menuflow.mappers.CategoryMapper;
import com.gabriel.springboot.app.menuflow.models.dto.request.product.CreateCategoryRequest;
import com.gabriel.springboot.app.menuflow.models.dto.request.product.UpdateCategoryRequest;
import com.gabriel.springboot.app.menuflow.models.dto.response.product.CategoryResponse;
import com.gabriel.springboot.app.menuflow.models.entities.Category;
import com.gabriel.springboot.app.menuflow.repositories.CategoryRepository;
import com.gabriel.springboot.app.menuflow.services.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.gabriel.springboot.app.menuflow.constants.ExceptionConstants.*;
import static com.gabriel.springboot.app.menuflow.constants.LocaleConstants.DEFAULT_LANG;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    @Transactional
    public CategoryResponse createCategory(CreateCategoryRequest request) {
        log.info("create category: {}", request.name());

        checkNameAvailability(request.name());

        Category category = Category.of();
        category.addTranslation(DEFAULT_LANG, request.name());

        Category saved = categoryRepository.save(category);

        log.info("create category successful - ID: {}", saved.getId());

        return categoryMapper.toResponse(saved, DEFAULT_LANG);
    }

    @Override
    @Transactional
    public CategoryResponse updateCategory(Long id, UpdateCategoryRequest request) {
        log.info("update category: {}", id);

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(CATEGORY_NOT_FOUND_MESSAGE));

        String currentName = category.getName(DEFAULT_LANG);
        if (currentName == null || !currentName.equals(request.name())) {
            checkNameAvailability(request.name());
            category.updateTranslation(DEFAULT_LANG, request.name());
        }

        Category updated = categoryRepository.save(category);

        log.info("update category successful - ID: {}", updated.getId());

        return categoryMapper.toResponse(updated, DEFAULT_LANG);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .map(category -> categoryMapper.toResponse(category, DEFAULT_LANG))
                .orElseThrow(() -> new ResourceNotFoundException(CATEGORY_NOT_FOUND_MESSAGE));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAllByLanguage(DEFAULT_LANG).stream()
                .map(category -> categoryMapper.toResponse(category, DEFAULT_LANG))
                .toList();
    }

    @Override
    @Transactional
    public void deleteCategoryById(Long id) {
        log.info("delete category: {}", id);

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(CATEGORY_NOT_FOUND_MESSAGE));

        if (!category.getDishes().isEmpty()) {
            throw new BusinessException(CATEGORY_HAS_DISHES_MESSAGE);
        }

        categoryRepository.delete(category);

        log.info("delete category successful - ID: {}", id);
    }

    private void checkNameAvailability(String name) {
        if (categoryRepository.existsByNameAndLang(name, DEFAULT_LANG)) {
            throw new BusinessException(CATEGORY_EXISTS_MESSAGE + name);
        }
    }
}
