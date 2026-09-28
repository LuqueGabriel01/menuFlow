package com.gabriel.springboot.app.menuflow.controllers;

import com.gabriel.springboot.app.menuflow.models.dto.request.product.CreateIngredientRequest;
import com.gabriel.springboot.app.menuflow.models.dto.request.product.UpdateIngredientRequest;
import com.gabriel.springboot.app.menuflow.models.dto.response.product.IngredientResponse;
import com.gabriel.springboot.app.menuflow.security.JwtUtil;
import com.gabriel.springboot.app.menuflow.services.IngredientService;
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

import static com.gabriel.springboot.app.menuflow.constants.ApiPaths.INGREDIENT_PATH;
import static com.gabriel.springboot.app.menuflow.constants.ExceptionConstants.INGREDIENT_DELETE_SUCCESS_MESSAGE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(IngredientController.class)
@AutoConfigureMockMvc(addFilters = false)
class IngredientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private IngredientService ingredientService;

    @Test
    @DisplayName("Should return a list of all ingredients with 200 OK")
    void getAllIngredients() throws Exception {
        when(ingredientService.getAllIngredients()).thenReturn(List.of());

        mockMvc.perform(get(INGREDIENT_PATH))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @DisplayName("Should return ingredient details when a valid ID is provided")
    void getIngredientById() throws Exception {
        IngredientResponse response = new IngredientResponse(1L, "Tomato");
        when(ingredientService.getIngredientById(1L)).thenReturn(response);

        mockMvc.perform(get(INGREDIENT_PATH + "/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Tomato"));
    }

    @Test
    @DisplayName("Should create a new ingredient and return 201 Created")
    void createIngredient() throws Exception {
        CreateIngredientRequest request = new CreateIngredientRequest("Tomato");
        IngredientResponse response = new IngredientResponse(1L, "Tomato");

        when(ingredientService.createIngredient(request)).thenReturn(response);

        mockMvc.perform(post(INGREDIENT_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.name").value("Tomato"));
    }

    @Test
    @DisplayName("Should update an existing ingredient and return 200 OK")
    void updateIngredient() throws Exception {
        UpdateIngredientRequest request = new UpdateIngredientRequest("Onion");
        when(ingredientService.updateIngredient(eq(1L), any())).thenReturn(new IngredientResponse(1L, "Onion"));

        mockMvc.perform(put(INGREDIENT_PATH + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Onion"));
    }

    @Test
    @DisplayName("Should remove an ingredient and return 204 No Content with success message")
    void deleteIngredient() throws Exception {
        doNothing().when(ingredientService).deleteIngredientById(1L);

        mockMvc.perform(delete(INGREDIENT_PATH + "/1"))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.message").value(INGREDIENT_DELETE_SUCCESS_MESSAGE));
    }
}
