package com.gabriel.springboot.app.menuflow.services;

import com.gabriel.springboot.app.menuflow.models.dto.request.product.CreateAllergenRequest;
import com.gabriel.springboot.app.menuflow.models.dto.request.product.UpdateAllergenRequest;
import com.gabriel.springboot.app.menuflow.models.dto.response.product.AllergenResponse;

import java.util.List;

public interface AllergenService {
    AllergenResponse createAllergen(CreateAllergenRequest request);
    AllergenResponse updateAllergen(Long id, UpdateAllergenRequest request);
    AllergenResponse getAllergenById(Long id);
    List<AllergenResponse> getAllAllergens();
    void deleteAllergenById(Long id);
}
