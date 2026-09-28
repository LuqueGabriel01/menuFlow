package com.gabriel.springboot.app.menuflow.controllers;

import com.gabriel.springboot.app.menuflow.models.dto.request.product.CreateAllergenRequest;
import com.gabriel.springboot.app.menuflow.models.dto.request.product.UpdateAllergenRequest;
import com.gabriel.springboot.app.menuflow.models.dto.response.product.AllergenResponse;
import com.gabriel.springboot.app.menuflow.security.JwtUtil;
import com.gabriel.springboot.app.menuflow.services.AllergenService;
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

import static com.gabriel.springboot.app.menuflow.constants.ApiPaths.ALLERGEN_PATH;
import static com.gabriel.springboot.app.menuflow.constants.ExceptionConstants.ALLERGEN_DELETE_SUCCESS_MESSAGE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AllergenController.class)
@AutoConfigureMockMvc(addFilters = false)
class AllergenControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private AllergenService allergenService;

    @Test
    @DisplayName("Should return a list of all allergens with 200 OK")
    void getAllAllergens() throws Exception {
        when(allergenService.getAllAllergens()).thenReturn(List.of());

        mockMvc.perform(get(ALLERGEN_PATH))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @DisplayName("Should return allergen details when a valid ID is provided")
    void getAllergenById() throws Exception {
        AllergenResponse response = new AllergenResponse(1L, "Gluten");
        when(allergenService.getAllergenById(1L)).thenReturn(response);

        mockMvc.perform(get(ALLERGEN_PATH + "/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Gluten"));
    }

    @Test
    @DisplayName("Should create a new allergen and return 201 Created")
    void createAllergen() throws Exception {
        CreateAllergenRequest request = new CreateAllergenRequest("Gluten");
        AllergenResponse response = new AllergenResponse(1L, "Gluten");

        when(allergenService.createAllergen(request)).thenReturn(response);

        mockMvc.perform(post(ALLERGEN_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.name").value("Gluten"));
    }

    @Test
    @DisplayName("Should update an existing allergen and return 200 OK")
    void updateAllergen() throws Exception {
        UpdateAllergenRequest request = new UpdateAllergenRequest("Lactose");
        when(allergenService.updateAllergen(eq(1L), any())).thenReturn(new AllergenResponse(1L, "Lactose"));

        mockMvc.perform(put(ALLERGEN_PATH + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Lactose"));
    }

    @Test
    @DisplayName("Should remove an allergen and return 204 No Content with success message")
    void deleteAllergen() throws Exception {
        doNothing().when(allergenService).deleteAllergenById(1L);

        mockMvc.perform(delete(ALLERGEN_PATH + "/1"))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.message").value(ALLERGEN_DELETE_SUCCESS_MESSAGE));
    }
}
