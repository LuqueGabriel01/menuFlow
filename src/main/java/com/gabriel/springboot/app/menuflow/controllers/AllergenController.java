package com.gabriel.springboot.app.menuflow.controllers;

import com.gabriel.springboot.app.menuflow.models.dto.request.product.CreateAllergenRequest;
import com.gabriel.springboot.app.menuflow.models.dto.request.product.UpdateAllergenRequest;
import com.gabriel.springboot.app.menuflow.models.dto.response.ApiResponse;
import com.gabriel.springboot.app.menuflow.models.dto.response.product.AllergenResponse;
import com.gabriel.springboot.app.menuflow.services.AllergenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.gabriel.springboot.app.menuflow.config.SwaggerConfig.SECURITY_SCHEME_NAME;
import static com.gabriel.springboot.app.menuflow.constants.ApiPaths.ALLERGEN_PATH;
import static com.gabriel.springboot.app.menuflow.constants.ApiPaths.PATH_ID;
import static com.gabriel.springboot.app.menuflow.constants.ExceptionConstants.ALLERGEN_DELETE_SUCCESS_MESSAGE;

@Slf4j
@RestController
@RequestMapping(ALLERGEN_PATH)
@Tag(name = "Allergen Management", description = "Endpoints for menu allergen management")
@SecurityRequirement(name = SECURITY_SCHEME_NAME)
@RequiredArgsConstructor
public class AllergenController {

    private final AllergenService allergenService;

    @GetMapping
    @Operation(summary = "List all allergens")
    public ResponseEntity<ApiResponse<List<AllergenResponse>>> getAllAllergens() {
        log.info("GET: {} - get list of allergens", ALLERGEN_PATH);

        List<AllergenResponse> allergens = allergenService.getAllAllergens();

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(allergens));
    }

    @GetMapping(PATH_ID)
    @Operation(summary = "Get allergen by ID")
    public ResponseEntity<ApiResponse<AllergenResponse>> getAllergen(@PathVariable Long id) {
        log.info("GET: {}/{} - get allergen", ALLERGEN_PATH, id);

        AllergenResponse response = allergenService.getAllergenById(id);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(response));
    }

    @PostMapping
    @Operation(summary = "Create allergen", description = "Create a new allergen. ADMIN and CHEF only.")
    public ResponseEntity<ApiResponse<AllergenResponse>> createAllergen(@Valid @RequestBody CreateAllergenRequest request) {
        log.info("POST: {} - create allergen: {}", ALLERGEN_PATH, request.name());

        AllergenResponse response = allergenService.createAllergen(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    @PutMapping(PATH_ID)
    @Operation(summary = "Update allergen")
    public ResponseEntity<ApiResponse<AllergenResponse>> updateAllergen(@PathVariable Long id, @Valid @RequestBody UpdateAllergenRequest request) {
        log.info("PUT: {}/{} - update allergen: {}", ALLERGEN_PATH, id, request.name());

        AllergenResponse response = allergenService.updateAllergen(id, request);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(response));
    }

    @DeleteMapping(PATH_ID)
    @Operation(summary = "Delete allergen")
    public ResponseEntity<ApiResponse<Void>> deleteAllergen(@PathVariable Long id) {
        log.info("DELETE: {}/{} - delete allergen", ALLERGEN_PATH, id);

        allergenService.deleteAllergenById(id);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ApiResponse.success(ALLERGEN_DELETE_SUCCESS_MESSAGE, null));
    }
}
