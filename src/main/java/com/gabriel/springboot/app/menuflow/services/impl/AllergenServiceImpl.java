package com.gabriel.springboot.app.menuflow.services.impl;

import com.gabriel.springboot.app.menuflow.exceptions.BusinessException;
import com.gabriel.springboot.app.menuflow.exceptions.ResourceNotFoundException;
import com.gabriel.springboot.app.menuflow.mappers.AllergenMapper;
import com.gabriel.springboot.app.menuflow.models.dto.request.product.CreateAllergenRequest;
import com.gabriel.springboot.app.menuflow.models.dto.request.product.UpdateAllergenRequest;
import com.gabriel.springboot.app.menuflow.models.dto.response.product.AllergenResponse;
import com.gabriel.springboot.app.menuflow.models.entities.Allergen;
import com.gabriel.springboot.app.menuflow.models.entities.Dish;
import com.gabriel.springboot.app.menuflow.repositories.AllergenRepository;
import com.gabriel.springboot.app.menuflow.services.AllergenService;
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
public class AllergenServiceImpl implements AllergenService {

    private final AllergenRepository allergenRepository;
    private final AllergenMapper allergenMapper;

    @Override
    @Transactional
    public AllergenResponse createAllergen(CreateAllergenRequest request) {
        log.info("create allergen: {}", request.name());

        checkNameAvailability(request.name());

        Allergen allergen = Allergen.of();
        allergen.addTranslation(DEFAULT_LANG, request.name());

        Allergen saved = allergenRepository.save(allergen);

        log.info("create allergen successful - ID: {}", saved.getId());

        return allergenMapper.toResponse(saved, DEFAULT_LANG);
    }

    @Override
    @Transactional
    public AllergenResponse updateAllergen(Long id, UpdateAllergenRequest request) {
        log.info("update allergen: {}", id);

        Allergen allergen = allergenRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ALLERGEN_NOT_FOUND_MESSAGE));

        String currentName = allergen.getName(DEFAULT_LANG);
        if (currentName == null || !currentName.equals(request.name())) {
            checkNameAvailability(request.name());
            allergen.updateTranslation(DEFAULT_LANG, request.name());
        }

        Allergen updated = allergenRepository.save(allergen);

        log.info("update allergen successful - ID: {}", updated.getId());

        return allergenMapper.toResponse(updated, DEFAULT_LANG);
    }

    @Override
    @Transactional(readOnly = true)
    public AllergenResponse getAllergenById(Long id) {
        return allergenRepository.findById(id)
                .map(allergen -> allergenMapper.toResponse(allergen, DEFAULT_LANG))
                .orElseThrow(() -> new ResourceNotFoundException(ALLERGEN_NOT_FOUND_MESSAGE));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AllergenResponse> getAllAllergens() {
        return allergenRepository.findAllByLanguage(DEFAULT_LANG).stream()
                .map(allergen -> allergenMapper.toResponse(allergen, DEFAULT_LANG))
                .toList();
    }

    @Override
    @Transactional
    public void deleteAllergenById(Long id) {
        log.info("delete allergen: {}", id);

        Allergen allergen = allergenRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ALLERGEN_NOT_FOUND_MESSAGE));

        List<Dish> dishes = new ArrayList<>(allergen.getDishes());
        dishes.forEach(allergen::removeDish);

        allergenRepository.delete(allergen);

        log.info("delete allergen successful - ID: {}", id);
    }

    private void checkNameAvailability(String name) {
        if (allergenRepository.existsByNameAndLang(name, DEFAULT_LANG)) {
            throw new BusinessException(ALLERGEN_EXISTS_MESSAGE + name);
        }
    }
}
