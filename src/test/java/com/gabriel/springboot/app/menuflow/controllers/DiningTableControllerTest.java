package com.gabriel.springboot.app.menuflow.controllers;

import com.gabriel.springboot.app.menuflow.models.dto.request.CreateTableRequest;
import com.gabriel.springboot.app.menuflow.models.dto.request.UpdateTableRequest;
import com.gabriel.springboot.app.menuflow.models.dto.response.TableResponse;
import com.gabriel.springboot.app.menuflow.security.JwtUtil;
import com.gabriel.springboot.app.menuflow.services.DiningTableService;
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

import static com.gabriel.springboot.app.menuflow.constants.ApiPaths.TABLE_PATH;
import static com.gabriel.springboot.app.menuflow.constants.ExceptionConstants.DELETE_SUCCESS_MESSAGE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DiningTableController.class)
@AutoConfigureMockMvc(addFilters = false)
class DiningTableControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private DiningTableService diningTableService;

    @Test
    @DisplayName("Should return a list of all tables with 200 OK")
    void getAllTables() throws Exception{
        when(diningTableService.getAllTables()).thenReturn(List.of());

        mockMvc.perform(get(TABLE_PATH))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @DisplayName("Should return table details when a valid ID is provided")
    void getTableById() throws Exception{
        TableResponse response = new TableResponse(1L, 5, true,"QR-123", null, null);
        when(diningTableService.getTableById(1L)).thenReturn(response);

        mockMvc.perform(get(TABLE_PATH+"/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.number").value(5))
                .andExpect(jsonPath("$.data.qrCode").value("QR-123"));
    }

    @Test
    @DisplayName("Should return table details when a valid QR code is provided")
    void getTableByCode() throws Exception{
        String qrCode = "QR-123";
        when(diningTableService.getTableByQrCode(qrCode)).thenReturn(new TableResponse(1L, 10, true, qrCode, null, null));

        mockMvc.perform(get(TABLE_PATH+"/qr/"+ qrCode))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.qrCode").value(qrCode));
    }

    @Test
    @DisplayName("Should create a new table and return 201 Created")
    void createTable() throws Exception{
        CreateTableRequest request = new CreateTableRequest(10,true);
        TableResponse response = new TableResponse(1L, 5, true,"QR-123", null, null);

        when(diningTableService.createTable(request)).thenReturn(response);

        mockMvc.perform(post(TABLE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.number").value(5));
    }

    @Test
    @DisplayName("Should update an existing table and return 200 OK")
    void updateTable() throws Exception {
        UpdateTableRequest request = new UpdateTableRequest(15, true);
        when(diningTableService.updateTable(eq(1L), any())).thenReturn(new TableResponse(1L, 15, true, "QR-321", null, null));

        mockMvc.perform(put(TABLE_PATH + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.number").value(15));
    }

    @Test
    @DisplayName("Should toggle table's active status and return updated table")
    void toggleTableStatus() throws Exception {
        when(diningTableService.toggleTableStatus(1L)).thenReturn(new TableResponse(1L, 5, false, "QR-123", null, null));

        mockMvc.perform(patch(TABLE_PATH + "/1/toggle"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.active").value(false));
    }

    @Test
    @DisplayName("Should remove a table and return 204 No Content with success message")
    void deleteTable() throws Exception {
        doNothing().when(diningTableService).deleteTableById(1L);

        mockMvc.perform(delete(TABLE_PATH + "/1"))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.message").value(DELETE_SUCCESS_MESSAGE));
    }
}