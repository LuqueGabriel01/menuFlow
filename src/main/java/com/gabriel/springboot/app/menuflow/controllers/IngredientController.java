package com.gabriel.springboot.app.menuflow.controllers;

import com.gabriel.springboot.app.menuflow.models.dto.request.product.CreateIngredientRequest;
import com.gabriel.springboot.app.menuflow.models.dto.request.product.UpdateIngredientRequest;
import com.gabriel.springboot.app.menuflow.models.dto.response.ApiResponse;
import com.gabriel.springboot.app.menuflow.models.dto.response.product.IngredientResponse;
import com.gabriel.springboot.app.menuflow.services.IngredientService;
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
import static com.gabriel.springboot.app.menuflow.constants.ApiPaths.INGREDIENT_PATH;
import static com.gabriel.springboot.app.menuflow.constants.ApiPaths.PATH_ID;
import static com.gabriel.springboot.app.menuflow.constants.ExceptionConstants.INGREDIENT_DELETE_SUCCESS_MESSAGE;

@Slf4j
@RestController
@RequestMapping(INGREDIENT_PATH)
@Tag(name = "Ingredient Management", description = "Endpoints for menu ingredient management")
@SecurityRequirement(name = SECURITY_SCHEME_NAME)
@RequiredArgsConstructor
public class IngredientController {

    private final IngredientService ingredientService;

    @GetMapping
    @Operation(summary = "List all ingredients")
    public ResponseEntity<ApiResponse<List<IngredientResponse>>> getAllIngredients() {
        log.info("GET: {} - get list of ingredients", INGREDIENT_PATH);

        List<IngredientResponse> ingredients = ingredientService.getAllIngredients();

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(ingredients));
    }

    @GetMapping(PATH_ID)
    @Operation(summary = "Get ingredient by ID")
    public ResponseEntity<ApiResponse<IngredientResponse>> getIngredient(@PathVariable Long id) {
        log.info("GET: {}/{} - get ingredient", INGREDIENT_PATH, id);

        IngredientResponse response = ingredientService.getIngredientById(id);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(response));
    }

    @PostMapping
    @Operation(summary = "Create ingredient", description = "Create a new ingredient. ADMIN and CHEF only.")
    public ResponseEntity<ApiResponse<IngredientResponse>> createIngredient(@Valid @RequestBody CreateIngredientRequest request) {
        log.info("POST: {} - create ingredient: {}", INGREDIENT_PATH, request.name());

        IngredientResponse response = ingredientService.createIngredient(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    @PutMapping(PATH_ID)
    @Operation(summary = "Update ingredient")
    public ResponseEntity<ApiResponse<IngredientResponse>> updateIngredient(@PathVariable Long id, @Valid @RequestBody UpdateIngredientRequest request) {
        log.info("PUT: {}/{} - update ingredient: {}", INGREDIENT_PATH, id, request.name());

        IngredientResponse response = ingredientService.updateIngredient(id, request);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(response));
    }

    @DeleteMapping(PATH_ID)
    @Operation(summary = "Delete ingredient")
    public ResponseEntity<ApiResponse<Void>> deleteIngredient(@PathVariable Long id) {
        log.info("DELETE: {}/{} - delete ingredient", INGREDIENT_PATH, id);

        ingredientService.deleteIngredientById(id);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ApiResponse.success(INGREDIENT_DELETE_SUCCESS_MESSAGE, null));
    }
}
