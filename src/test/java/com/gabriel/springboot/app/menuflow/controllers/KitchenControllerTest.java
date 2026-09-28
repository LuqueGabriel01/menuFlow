package com.gabriel.springboot.app.menuflow.controllers;

import com.gabriel.springboot.app.menuflow.models.dto.response.order.OrderResponse;
import com.gabriel.springboot.app.menuflow.models.entities.enums.OrderStatus;
import com.gabriel.springboot.app.menuflow.security.JwtUtil;
import com.gabriel.springboot.app.menuflow.services.OrderService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static com.gabriel.springboot.app.menuflow.constants.ApiPaths.KITCHEN_PATH;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(KitchenController.class)
@AutoConfigureMockMvc(addFilters = false)
class KitchenControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private OrderService orderService;

    private OrderResponse sampleResponse() {
        return OrderResponse.builder()
                .id(1L)
                .sessionId(1L)
                .tableId(1L)
                .tableNumber(5)
                .status(OrderStatus.PENDING)
                .totalAmount(BigDecimal.TEN)
                .items(Collections.emptyList())
                .build();
    }

    @Test
    @DisplayName("Should return the kitchen queue with 200 OK")
    void getQueue() throws Exception {
        when(orderService.getKitchenQueue()).thenReturn(List.of(sampleResponse()));

        mockMvc.perform(get(KITCHEN_PATH + "/queue"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].status").value("PENDING"));
    }
}
