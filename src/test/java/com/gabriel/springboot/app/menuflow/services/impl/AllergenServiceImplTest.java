package com.gabriel.springboot.app.menuflow.services.impl;

import com.gabriel.springboot.app.menuflow.exceptions.BusinessException;
import com.gabriel.springboot.app.menuflow.exceptions.ResourceNotFoundException;
import com.gabriel.springboot.app.menuflow.mappers.AllergenMapper;
import com.gabriel.springboot.app.menuflow.models.dto.request.product.CreateAllergenRequest;
import com.gabriel.springboot.app.menuflow.models.dto.request.product.UpdateAllergenRequest;
import com.gabriel.springboot.app.menuflow.models.dto.response.product.AllergenResponse;
import com.gabriel.springboot.app.menuflow.models.entities.Allergen;
import com.gabriel.springboot.app.menuflow.repositories.AllergenRepository;
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
class AllergenServiceImplTest {

    @Mock
    private AllergenRepository allergenRepository;

    @Mock
    private AllergenMapper allergenMapper;

    @InjectMocks
    private AllergenServiceImpl allergenService;

    @Test
    @DisplayName("Should create allergen successfully when name does not exist")
    void createAllergen() {
        CreateAllergenRequest request = new CreateAllergenRequest("Gluten");

        Allergen allergen = Allergen.of();
        allergen.addTranslation(DEFAULT_LANG, "Gluten");
        AllergenResponse response = new AllergenResponse(1L, "Gluten");

        when(allergenRepository.existsByNameAndLang("Gluten", DEFAULT_LANG)).thenReturn(false);
        when(allergenRepository.save(any(Allergen.class))).thenReturn(allergen);
        when(allergenMapper.toResponse(allergen, DEFAULT_LANG)).thenReturn(response);

        AllergenResponse result = allergenService.createAllergen(request);

        assertEquals("Gluten", result.name());
        verify(allergenRepository).save(any(Allergen.class));
    }

    @Test
    @DisplayName("Should throw BusinessException when creating allergen with existing name")
    void createAllergenAlreadyExists() {
        CreateAllergenRequest request = new CreateAllergenRequest("Gluten");

        when(allergenRepository.existsByNameAndLang("Gluten", DEFAULT_LANG)).thenReturn(true);

        assertThrows(BusinessException.class, () -> allergenService.createAllergen(request));

        verify(allergenRepository, never()).save(any(Allergen.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when allergen does not exist")
    void getAllergenByIdNotFound() {
        when(allergenRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> allergenService.getAllergenById(1L));
    }

    @Test
    @DisplayName("Should update allergen name when new name is available")
    void updateAllergen() {
        UpdateAllergenRequest request = new UpdateAllergenRequest("Lactose");

        Allergen allergen = Allergen.of();
        allergen.addTranslation(DEFAULT_LANG, "Gluten");
        AllergenResponse response = new AllergenResponse(1L, "Lactose");

        when(allergenRepository.findById(1L)).thenReturn(Optional.of(allergen));
        when(allergenRepository.existsByNameAndLang("Lactose", DEFAULT_LANG)).thenReturn(false);
        when(allergenRepository.save(allergen)).thenReturn(allergen);
        when(allergenMapper.toResponse(allergen, DEFAULT_LANG)).thenReturn(response);

        AllergenResponse result = allergenService.updateAllergen(1L, request);

        assertEquals("Lactose", result.name());
    }

    @Test
    @DisplayName("Should return list of all allergens")
    void getAllAllergens() {
        when(allergenRepository.findAllByLanguage(DEFAULT_LANG)).thenReturn(List.of());

        List<AllergenResponse> result = allergenService.getAllAllergens();

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should delete allergen successfully")
    void deleteAllergen() {
        Allergen allergen = Allergen.of();

        when(allergenRepository.findById(1L)).thenReturn(Optional.of(allergen));

        allergenService.deleteAllergenById(1L);

        verify(allergenRepository).delete(allergen);
    }
}
