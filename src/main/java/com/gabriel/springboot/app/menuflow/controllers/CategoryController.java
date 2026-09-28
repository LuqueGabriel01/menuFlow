package com.gabriel.springboot.app.menuflow.controllers;

import com.gabriel.springboot.app.menuflow.models.dto.request.product.CreateCategoryRequest;
import com.gabriel.springboot.app.menuflow.models.dto.request.product.UpdateCategoryRequest;
import com.gabriel.springboot.app.menuflow.models.dto.response.ApiResponse;
import com.gabriel.springboot.app.menuflow.models.dto.response.product.CategoryResponse;
import com.gabriel.springboot.app.menuflow.services.CategoryService;
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
import static com.gabriel.springboot.app.menuflow.constants.ApiPaths.CATEGORY_PATH;
import static com.gabriel.springboot.app.menuflow.constants.ApiPaths.PATH_ID;
import static com.gabriel.springboot.app.menuflow.constants.ExceptionConstants.CATEGORY_DELETE_SUCCESS_MESSAGE;

@Slf4j
@RestController
@RequestMapping(CATEGORY_PATH)
@Tag(name = "Category Management", description = "Endpoints for menu category management")
@SecurityRequirement(name = SECURITY_SCHEME_NAME)
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    @Operation(summary = "List all categories")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAllCategories() {
        log.info("GET: {} - get list of categories", CATEGORY_PATH);

        List<CategoryResponse> categories = categoryService.getAllCategories();

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(categories));
    }

    @GetMapping(PATH_ID)
    @Operation(summary = "Get category by ID")
    public ResponseEntity<ApiResponse<CategoryResponse>> getCategory(@PathVariable Long id) {
        log.info("GET: {}/{} - get category", CATEGORY_PATH, id);

        CategoryResponse response = categoryService.getCategoryById(id);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(response));
    }

    @PostMapping
    @Operation(summary = "Create category", description = "Create a new menu category. ADMIN and CHEF only.")
    public ResponseEntity<ApiResponse<CategoryResponse>> createCategory(@Valid @RequestBody CreateCategoryRequest request) {
        log.info("POST: {} - create category: {}", CATEGORY_PATH, request.name());

        CategoryResponse response = categoryService.createCategory(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    @PutMapping(PATH_ID)
    @Operation(summary = "Update category")
    public ResponseEntity<ApiResponse<CategoryResponse>> updateCategory(@PathVariable Long id, @Valid @RequestBody UpdateCategoryRequest request) {
        log.info("PUT: {}/{} - update category: {}", CATEGORY_PATH, id, request.name());

        CategoryResponse response = categoryService.updateCategory(id, request);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(response));
    }

    @DeleteMapping(PATH_ID)
    @Operation(summary = "Delete category", description = "Cannot be deleted if it has associated dishes.")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable Long id) {
        log.info("DELETE: {}/{} - delete category", CATEGORY_PATH, id);

        categoryService.deleteCategoryById(id);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ApiResponse.success(CATEGORY_DELETE_SUCCESS_MESSAGE, null));
    }
}
