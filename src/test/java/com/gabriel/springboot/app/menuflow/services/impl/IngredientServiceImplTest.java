package com.gabriel.springboot.app.menuflow.services.impl;

import com.gabriel.springboot.app.menuflow.exceptions.BusinessException;
import com.gabriel.springboot.app.menuflow.exceptions.ResourceNotFoundException;
import com.gabriel.springboot.app.menuflow.mappers.IngredientMapper;
import com.gabriel.springboot.app.menuflow.models.dto.request.product.CreateIngredientRequest;
import com.gabriel.springboot.app.menuflow.models.dto.request.product.UpdateIngredientRequest;
import com.gabriel.springboot.app.menuflow.models.dto.response.product.IngredientResponse;
import com.gabriel.springboot.app.menuflow.models.entities.Ingredient;
import com.gabriel.springboot.app.menuflow.repositories.IngredientRepository;
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
class IngredientServiceImplTest {

    @Mock
    private IngredientRepository ingredientRepository;

    @Mock
    private IngredientMapper ingredientMapper;

    @InjectMocks
    private IngredientServiceImpl ingredientService;

    @Test
    @DisplayName("Should create ingredient successfully when name does not exist")
    void createIngredient() {
        CreateIngredientRequest request = new CreateIngredientRequest("Tomato");

        Ingredient ingredient = Ingredient.of();
        ingredient.addTranslation(DEFAULT_LANG, "Tomato");
        IngredientResponse response = new IngredientResponse(1L, "Tomato");

        when(ingredientRepository.existsNameAndLang("Tomato", DEFAULT_LANG)).thenReturn(false);
        when(ingredientRepository.save(any(Ingredient.class))).thenReturn(ingredient);
        when(ingredientMapper.toResponse(ingredient, DEFAULT_LANG)).thenReturn(response);

        IngredientResponse result = ingredientService.createIngredient(request);

        assertEquals("Tomato", result.name());
        verify(ingredientRepository).save(any(Ingredient.class));
    }

    @Test
    @DisplayName("Should throw BusinessException when creating ingredient with existing name")
    void createIngredientAlreadyExists() {
        CreateIngredientRequest request = new CreateIngredientRequest("Tomato");

        when(ingredientRepository.existsNameAndLang("Tomato", DEFAULT_LANG)).thenReturn(true);

        assertThrows(BusinessException.class, () -> ingredientService.createIngredient(request));

        verify(ingredientRepository, never()).save(any(Ingredient.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when ingredient does not exist")
    void getIngredientByIdNotFound() {
        when(ingredientRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> ingredientService.getIngredientById(1L));
    }

    @Test
    @DisplayName("Should update ingredient name when new name is available")
    void updateIngredient() {
        UpdateIngredientRequest request = new UpdateIngredientRequest("Onion");

        Ingredient ingredient = Ingredient.of();
        ingredient.addTranslation(DEFAULT_LANG, "Tomato");
        IngredientResponse response = new IngredientResponse(1L, "Onion");

        when(ingredientRepository.findById(1L)).thenReturn(Optional.of(ingredient));
        when(ingredientRepository.existsNameAndLang("Onion", DEFAULT_LANG)).thenReturn(false);
        when(ingredientRepository.save(ingredient)).thenReturn(ingredient);
        when(ingredientMapper.toResponse(ingredient, DEFAULT_LANG)).thenReturn(response);

        IngredientResponse result = ingredientService.updateIngredient(1L, request);

        assertEquals("Onion", result.name());
    }

    @Test
    @DisplayName("Should return list of all ingredients")
    void getAllIngredients() {
        when(ingredientRepository.findAllByLanguage(DEFAULT_LANG)).thenReturn(List.of());

        List<IngredientResponse> result = ingredientService.getAllIngredients();

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should delete ingredient successfully")
    void deleteIngredient() {
        Ingredient ingredient = Ingredient.of();

        when(ingredientRepository.findById(1L)).thenReturn(Optional.of(ingredient));

        ingredientService.deleteIngredientById(1L);

        verify(ingredientRepository).delete(ingredient);
    }
}
