package com.gabriel.springboot.app.menuflow.controllers;

import com.gabriel.springboot.app.menuflow.models.dto.request.product.CreateDishRequest;
import com.gabriel.springboot.app.menuflow.models.dto.request.product.UpdateDishRequest;
import com.gabriel.springboot.app.menuflow.models.dto.response.product.DishResponse;
import com.gabriel.springboot.app.menuflow.security.JwtUtil;
import com.gabriel.springboot.app.menuflow.services.DishService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static com.gabriel.springboot.app.menuflow.constants.ApiPaths.DISH_PATH;
import static com.gabriel.springboot.app.menuflow.constants.ExceptionConstants.DISH_DELETE_SUCCESS_MESSAGE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DishController.class)
@AutoConfigureMockMvc(addFilters = false)
class DishControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private DishService dishService;

    private DishResponse sampleResponse() {
        return new DishResponse(1L, 1L, "Mains", "Pizza", "Cheese and tomato pizza",
                BigDecimal.TEN, true, null, null, null, "chef",
                Collections.emptyList(), Collections.emptyList());
    }

    @Test
    @DisplayName("Should return a list of all dishes with 200 OK")
    void getAllDishes() throws Exception {
        when(dishService.getAllDishes()).thenReturn(List.of());

        mockMvc.perform(get(DISH_PATH))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @DisplayName("Should return a list of available dishes with 200 OK")
    void getAvailableDishes() throws Exception {
        when(dishService.getAvailableDishes()).thenReturn(List.of(sampleResponse()));

        mockMvc.perform(get(DISH_PATH + "/available"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].available").value(true));
    }

    @Test
    @DisplayName("Should return dishes filtered by category")
    void getDishesByCategory() throws Exception {
        when(dishService.getDishesByCategory(1L)).thenReturn(List.of(sampleResponse()));

        mockMvc.perform(get(DISH_PATH + "/category/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].categoryId").value(1));
    }

    @Test
    @DisplayName("Should return dish details when a valid ID is provided")
    void getDishById() throws Exception {
        when(dishService.getDishById(1L)).thenReturn(sampleResponse());

        mockMvc.perform(get(DISH_PATH + "/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Pizza"));
    }

    @Test
    @DisplayName("Should create a new dish and return 201 Created")
    void createDish() throws Exception {
        CreateDishRequest request = new CreateDishRequest(1L, "Pizza", "Cheese and tomato pizza", BigDecimal.TEN, true, null, null);

        when(dishService.createDish(request)).thenReturn(sampleResponse());

        mockMvc.perform(post(DISH_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.name").value("Pizza"));
    }

    @Test
    @DisplayName("Should update an existing dish and return 200 OK")
    void updateDish() throws Exception {
        UpdateDishRequest request = new UpdateDishRequest(1L, "Pizza", "Updated description here", BigDecimal.valueOf(12), true, null, null);

        when(dishService.updateDish(eq(1L), any())).thenReturn(sampleResponse());

        mockMvc.perform(put(DISH_PATH + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Pizza"));
    }

    @Test
    @DisplayName("Should remove a dish and return 204 No Content with success message")
    void deleteDish() throws Exception {
        doNothing().when(dishService).deleteDishById(1L);

        mockMvc.perform(delete(DISH_PATH + "/1"))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.message").value(DISH_DELETE_SUCCESS_MESSAGE));
    }
}
