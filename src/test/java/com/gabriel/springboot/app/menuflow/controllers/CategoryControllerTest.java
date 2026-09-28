package com.gabriel.springboot.app.menuflow.controllers;

import com.gabriel.springboot.app.menuflow.models.dto.request.product.CreateCategoryRequest;
import com.gabriel.springboot.app.menuflow.models.dto.request.product.UpdateCategoryRequest;
import com.gabriel.springboot.app.menuflow.models.dto.response.product.CategoryResponse;
import com.gabriel.springboot.app.menuflow.security.JwtUtil;
import com.gabriel.springboot.app.menuflow.services.CategoryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static com.gabriel.springboot.app.menuflow.constants.ApiPaths.CATEGORY_PATH;
import static com.gabriel.springboot.app.menuflow.constants.ExceptionConstants.CATEGORY_DELETE_SUCCESS_MESSAGE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CategoryController.class)
@AutoConfigureMockMvc(addFilters = false)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private CategoryService categoryService;

    @Test
    @DisplayName("Should return a list of all categories with 200 OK")
    void getAllCategories() throws Exception {
        when(categoryService.getAllCategories()).thenReturn(List.of());

        mockMvc.perform(get(CATEGORY_PATH))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @DisplayName("Should return category details when a valid ID is provided")
    void getCategoryById() throws Exception {
        CategoryResponse response = new CategoryResponse(1L, "Starters", 3);
        when(categoryService.getCategoryById(1L)).thenReturn(response);

        mockMvc.perform(get(CATEGORY_PATH + "/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Starters"))
                .andExpect(jsonPath("$.data.dishCount").value(3));
    }

    @Test
    @DisplayName("Should create a new category and return 201 Created")
    void createCategory() throws Exception {
        CreateCategoryRequest request = new CreateCategoryRequest("Starters");
        CategoryResponse response = new CategoryResponse(1L, "Starters", 0);

        when(categoryService.createCategory(request)).thenReturn(response);

        mockMvc.perform(post(CATEGORY_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.name").value("Starters"));
    }

    @Test
    @DisplayName("Should update an existing category and return 200 OK")
    void updateCategory() throws Exception {
        UpdateCategoryRequest request = new UpdateCategoryRequest("Mains");
        when(categoryService.updateCategory(eq(1L), any())).thenReturn(new CategoryResponse(1L, "Mains", 0));

        mockMvc.perform(put(CATEGORY_PATH + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Mains"));
    }

    @Test
    @DisplayName("Should remove a category and return 204 No Content with success message")
    void deleteCategory() throws Exception {
        doNothing().when(categoryService).deleteCategoryById(1L);

        mockMvc.perform(delete(CATEGORY_PATH + "/1"))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.message").value(CATEGORY_DELETE_SUCCESS_MESSAGE));
    }
}
