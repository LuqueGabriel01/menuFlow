package com.gabriel.springboot.app.menuflow.services.impl;

import com.gabriel.springboot.app.menuflow.exceptions.ResourceNotFoundException;
import com.gabriel.springboot.app.menuflow.mappers.DishMapper;
import com.gabriel.springboot.app.menuflow.models.dto.request.product.CreateDishRequest;
import com.gabriel.springboot.app.menuflow.models.dto.request.product.UpdateDishRequest;
import com.gabriel.springboot.app.menuflow.models.dto.response.product.DishResponse;
import com.gabriel.springboot.app.menuflow.models.entities.Allergen;
import com.gabriel.springboot.app.menuflow.models.entities.Category;
import com.gabriel.springboot.app.menuflow.models.entities.Dish;
import com.gabriel.springboot.app.menuflow.models.entities.Ingredient;
import com.gabriel.springboot.app.menuflow.models.entities.User;
import com.gabriel.springboot.app.menuflow.repositories.AllergenRepository;
import com.gabriel.springboot.app.menuflow.repositories.CategoryRepository;
import com.gabriel.springboot.app.menuflow.repositories.DishRepository;
import com.gabriel.springboot.app.menuflow.repositories.IngredientRepository;
import com.gabriel.springboot.app.menuflow.repositories.UserRepository;
import com.gabriel.springboot.app.menuflow.services.DishService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.gabriel.springboot.app.menuflow.constants.ExceptionConstants.*;
import static com.gabriel.springboot.app.menuflow.constants.LocaleConstants.DEFAULT_LANG;

@Slf4j
@Service
@RequiredArgsConstructor
public class DishServiceImpl implements DishService {

    private final DishRepository dishRepository;
    private final CategoryRepository categoryRepository;
    private final IngredientRepository ingredientRepository;
    private final AllergenRepository allergenRepository;
    private final UserRepository userRepository;
    private final DishMapper dishMapper;

    @Override
    @Transactional
    public DishResponse createDish(CreateDishRequest request) {
        log.info("create dish: {}", request.name());

        Category category = findCategory(request.categoryId());
        User createdBy = getCurrentUser();

        Dish dish = Dish.of(category, request.price(), createdBy);
        dish.addTranslation(DEFAULT_LANG, request.name(), request.description());

        if (Boolean.FALSE.equals(request.available())) {
            dish.unavailable();
        }

        attachIngredients(dish, request.ingredientsIds());
        attachAllergens(dish, request.allergensIds());

        Dish saved = dishRepository.save(dish);

        log.info("create dish successful - ID: {}", saved.getId());

        return dishMapper.toResponse(saved, DEFAULT_LANG);
    }

    @Override
    @Transactional
    public DishResponse updateDish(Long id, UpdateDishRequest request) {
        log.info("update dish: {}", id);

        Dish dish = dishRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(DISH_NOT_FOUND_MESSAGE));

        if (!dish.getCategory().getId().equals(request.categoryId())) {
            dish.assignCategory(findCategory(request.categoryId()));
        }

        dish.assignPrice(request.price());
        dish.updateTranslation(DEFAULT_LANG, request.name(), request.description());

        if (request.available()) {
            dish.available();
        } else {
            dish.unavailable();
        }

        syncIngredients(dish, request.ingredientsIds());
        syncAllergens(dish, request.allergensIds());

        Dish updated = dishRepository.save(dish);

        log.info("update dish successful - ID: {}", updated.getId());

        return dishMapper.toResponse(updated, DEFAULT_LANG);
    }

    @Override
    @Transactional(readOnly = true)
    public DishResponse getDishById(Long id) {
        return dishRepository.findById(id)
                .map(dish -> dishMapper.toResponse(dish, DEFAULT_LANG))
                .orElseThrow(() -> new ResourceNotFoundException(DISH_NOT_FOUND_MESSAGE));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DishResponse> getAllDishes() {
        return dishRepository.findAllByLanguage(DEFAULT_LANG).stream()
                .map(dish -> dishMapper.toResponse(dish, DEFAULT_LANG))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DishResponse> getAvailableDishes() {
        return dishRepository.findByAvailableTrue().stream()
                .map(dish -> dishMapper.toResponse(dish, DEFAULT_LANG))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DishResponse> getDishesByCategory(Long categoryId) {
        return dishRepository.findByCategoryId(categoryId).stream()
                .map(dish -> dishMapper.toResponse(dish, DEFAULT_LANG))
                .toList();
    }

    @Override
    @Transactional
    public void deleteDishById(Long id) {
        log.info("delete dish: {}", id);

        Dish dish = dishRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(DISH_NOT_FOUND_MESSAGE));

        new ArrayList<>(dish.getIngredients()).forEach(dish::removeIngredient);
        new ArrayList<>(dish.getAllergens()).forEach(dish::removeAllergen);

        dishRepository.delete(dish);

        log.info("delete dish successful - ID: {}", id);
    }

    private Category findCategory(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException(CATEGORY_NOT_FOUND_MESSAGE));
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return null;
        }
        return userRepository.findByUsername(authentication.getName()).orElse(null);
    }

    private void attachIngredients(Dish dish, List<Long> ingredientIds) {
        if (ingredientIds == null) return;
        ingredientIds.forEach(ingredientId -> {
            Ingredient ingredient = ingredientRepository.findById(ingredientId)
                    .orElseThrow(() -> new ResourceNotFoundException(INGREDIENT_NOT_FOUND_MESSAGE));
            dish.addIngredient(ingredient);
        });
    }

    private void attachAllergens(Dish dish, List<Long> allergenIds) {
        if (allergenIds == null) return;
        allergenIds.forEach(allergenId -> {
            Allergen allergen = allergenRepository.findById(allergenId)
                    .orElseThrow(() -> new ResourceNotFoundException(ALLERGEN_NOT_FOUND_MESSAGE));
            dish.addAllergen(allergen);
        });
    }

    private void syncIngredients(Dish dish, List<Long> ingredientIds) {
        new ArrayList<>(dish.getIngredients()).forEach(dish::removeIngredient);
        attachIngredients(dish, ingredientIds);
    }

    private void syncAllergens(Dish dish, List<Long> allergenIds) {
        new ArrayList<>(dish.getAllergens()).forEach(dish::removeAllergen);
        attachAllergens(dish, allergenIds);
    }
}
