package com.gabriel.springboot.app.menuflow.security;

import com.gabriel.springboot.app.menuflow.security.filter.JwtAuthenticationFilter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@Import(SpringSecurityConfig.class)
class SpringSecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    @DisplayName("Public: login must be accessible to everyone")
    void publicEndpoints_Login_ShouldBeAccessible() throws Exception {
        mockMvc.perform(post("/api/auth/login"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Public: GET requests should be allowed without a token")
    void publicEndpoints_GetDishes_ShouldBePermitted() throws Exception {
        mockMvc.perform(get("/api/dishes"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "CASHIER")
    @DisplayName("CASHIER: Can view invoices")
    void cashierRole_CanAccessInvoices() throws Exception {
        mockMvc.perform(get("/api/invoices"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "CHEF")
    @DisplayName("CHEF: Can create categories")
    void chefRole_CanCreateCategories() throws Exception {
        mockMvc.perform(post("/api/categories"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "KITCHEN")
    @DisplayName("KITCHEN: You have access to the kitchen")
    void kitchenRole_CanAccessKitchen() throws Exception {
        mockMvc.perform(get("/api/kitchen/orders"))
                .andExpect(status().isOk());
    }
}