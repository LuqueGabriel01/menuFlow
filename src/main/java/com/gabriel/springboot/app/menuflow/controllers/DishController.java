package com.gabriel.springboot.app.menuflow.controllers;

import com.gabriel.springboot.app.menuflow.models.dto.request.product.CreateDishRequest;
import com.gabriel.springboot.app.menuflow.models.dto.request.product.UpdateDishRequest;
import com.gabriel.springboot.app.menuflow.models.dto.response.ApiResponse;
import com.gabriel.springboot.app.menuflow.models.dto.response.product.DishResponse;
import com.gabriel.springboot.app.menuflow.services.DishService;
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
import static com.gabriel.springboot.app.menuflow.constants.ApiPaths.*;
import static com.gabriel.springboot.app.menuflow.constants.ExceptionConstants.DISH_DELETE_SUCCESS_MESSAGE;

@Slf4j
@RestController
@RequestMapping(DISH_PATH)
@Tag(name = "Dish Management", description = "Endpoints for menu dish management")
@SecurityRequirement(name = SECURITY_SCHEME_NAME)
@RequiredArgsConstructor
public class DishController {

    private final DishService dishService;

    @GetMapping
    @Operation(summary = "List all dishes")
    public ResponseEntity<ApiResponse<List<DishResponse>>> getAllDishes() {
        log.info("GET: {} - get list of dishes", DISH_PATH);

        List<DishResponse> dishes = dishService.getAllDishes();

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(dishes));
    }

    @GetMapping(AVAILABLE)
    @Operation(summary = "List available dishes", description = "Retrieves only the dishes marked as available")
    public ResponseEntity<ApiResponse<List<DishResponse>>> getAvailableDishes() {
        log.info("GET: {} - get list of available dishes", DISH_PATH + AVAILABLE);

        List<DishResponse> dishes = dishService.getAvailableDishes();

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(dishes));
    }

    @GetMapping(PATH_CATEGORY_ID)
    @Operation(summary = "List dishes by category")
    public ResponseEntity<ApiResponse<List<DishResponse>>> getDishesByCategory(@PathVariable Long categoryId) {
        log.info("GET: {}/category/{} - get dishes by category", DISH_PATH, categoryId);

        List<DishResponse> dishes = dishService.getDishesByCategory(categoryId);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(dishes));
    }

    @GetMapping(PATH_ID)
    @Operation(summary = "Get dish by ID")
    public ResponseEntity<ApiResponse<DishResponse>> getDish(@PathVariable Long id) {
        log.info("GET: {}/{} - get dish", DISH_PATH, id);

        DishResponse response = dishService.getDishById(id);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(response));
    }

    @PostMapping
    @Operation(summary = "Create dish", description = "Create a new dish. ADMIN and CHEF only.")
    public ResponseEntity<ApiResponse<DishResponse>> createDish(@Valid @RequestBody CreateDishRequest request) {
        log.info("POST: {} - create dish: {}", DISH_PATH, request.name());

        DishResponse response = dishService.createDish(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    @PutMapping(PATH_ID)
    @Operation(summary = "Update dish")
    public ResponseEntity<ApiResponse<DishResponse>> updateDish(@PathVariable Long id, @Valid @RequestBody UpdateDishRequest request) {
        log.info("PUT: {}/{} - update dish: {}", DISH_PATH, id, request.name());

        DishResponse response = dishService.updateDish(id, request);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(response));
    }

    @DeleteMapping(PATH_ID)
    @Operation(summary = "Delete dish")
    public ResponseEntity<ApiResponse<Void>> deleteDish(@PathVariable Long id) {
        log.info("DELETE: {}/{} - delete dish", DISH_PATH, id);

        dishService.deleteDishById(id);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ApiResponse.success(DISH_DELETE_SUCCESS_MESSAGE, null));
    }
}
