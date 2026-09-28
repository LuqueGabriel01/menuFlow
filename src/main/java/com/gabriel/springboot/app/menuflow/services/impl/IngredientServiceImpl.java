package com.gabriel.springboot.app.menuflow.services.impl;

import com.gabriel.springboot.app.menuflow.exceptions.BusinessException;
import com.gabriel.springboot.app.menuflow.exceptions.ResourceNotFoundException;
import com.gabriel.springboot.app.menuflow.mappers.IngredientMapper;
import com.gabriel.springboot.app.menuflow.models.dto.request.product.CreateIngredientRequest;
import com.gabriel.springboot.app.menuflow.models.dto.request.product.UpdateIngredientRequest;
import com.gabriel.springboot.app.menuflow.models.dto.response.product.IngredientResponse;
import com.gabriel.springboot.app.menuflow.models.entities.Dish;
import com.gabriel.springboot.app.menuflow.models.entities.Ingredient;
import com.gabriel.springboot.app.menuflow.repositories.IngredientRepository;
import com.gabriel.springboot.app.menuflow.services.IngredientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.gabriel.springboot.app.menuflow.constants.ExceptionConstants.*;
import static com.gabriel.springboot.app.menuflow.constants.LocaleConstants.DEFAULT_LANG;

@Slf4j
@Service
@RequiredArgsConstructor
public class IngredientServiceImpl implements IngredientService {

    private final IngredientRepository ingredientRepository;
    private final IngredientMapper ingredientMapper;

    @Override
    @Transactional
    public IngredientResponse createIngredient(CreateIngredientRequest request) {
        log.info("create ingredient: {}", request.name());

        checkNameAvailability(request.name());

        Ingredient ingredient = Ingredient.of();
        ingredient.addTranslation(DEFAULT_LANG, request.name());

        Ingredient saved = ingredientRepository.save(ingredient);

        log.info("create ingredient successful - ID: {}", saved.getId());

        return ingredientMapper.toResponse(saved, DEFAULT_LANG);
    }

    @Override
    @Transactional
    public IngredientResponse updateIngredient(Long id, UpdateIngredientRequest request) {
        log.info("update ingredient: {}", id);

        Ingredient ingredient = ingredientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(INGREDIENT_NOT_FOUND_MESSAGE));

        String currentName = ingredient.getName(DEFAULT_LANG);
        if (currentName == null || !currentName.equals(request.name())) {
            checkNameAvailability(request.name());
            ingredient.updateTranslation(DEFAULT_LANG, request.name());
        }

        Ingredient updated = ingredientRepository.save(ingredient);

        log.info("update ingredient successful - ID: {}", updated.getId());

        return ingredientMapper.toResponse(updated, DEFAULT_LANG);
    }

    @Override
    @Transactional(readOnly = true)
    public IngredientResponse getIngredientById(Long id) {
        return ingredientRepository.findById(id)
                .map(ingredient -> ingredientMapper.toResponse(ingredient, DEFAULT_LANG))
                .orElseThrow(() -> new ResourceNotFoundException(INGREDIENT_NOT_FOUND_MESSAGE));
    }

    @Override
    @Transactional(readOnly = true)
    public List<IngredientResponse> getAllIngredients() {
        return ingredientRepository.findAllByLanguage(DEFAULT_LANG).stream()
                .map(ingredient -> ingredientMapper.toResponse(ingredient, DEFAULT_LANG))
                .toList();
    }

    @Override
    @Transactional
    public void deleteIngredientById(Long id) {
        log.info("delete ingredient: {}", id);

        Ingredient ingredient = ingredientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(INGREDIENT_NOT_FOUND_MESSAGE));

        List<Dish> dishes = new ArrayList<>(ingredient.getDishes());
        dishes.forEach(ingredient::removeDish);

        ingredientRepository.delete(ingredient);

        log.info("delete ingredient successful - ID: {}", id);
    }

    private void checkNameAvailability(String name) {
        if (ingredientRepository.existsNameAndLang(name, DEFAULT_LANG)) {
            throw new BusinessException(INGREDIENT_EXISTS_MESSAGE + name);
        }
    }
}
