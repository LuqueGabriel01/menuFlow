package com.gabriel.springboot.app.menuflow.security;

import com.gabriel.springboot.app.menuflow.config.JacksonConfig;
import com.gabriel.springboot.app.menuflow.controllers.AuthController;
import com.gabriel.springboot.app.menuflow.controllers.CategoryController;
import com.gabriel.springboot.app.menuflow.controllers.DishController;
import com.gabriel.springboot.app.menuflow.controllers.InvoiceController;
import com.gabriel.springboot.app.menuflow.controllers.KitchenController;
import com.gabriel.springboot.app.menuflow.exceptions.handler.CustomAccessDenied;
import com.gabriel.springboot.app.menuflow.exceptions.handler.CustomAuthenticationEntryPoint;
import com.gabriel.springboot.app.menuflow.security.filter.JwtAuthenticationFilter;
import com.gabriel.springboot.app.menuflow.services.AuthService;
import com.gabriel.springboot.app.menuflow.services.CategoryService;
import com.gabriel.springboot.app.menuflow.services.DishService;
import com.gabriel.springboot.app.menuflow.services.InvoiceService;
import com.gabriel.springboot.app.menuflow.services.OrderService;
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

@WebMvcTest(controllers = {AuthController.class, DishController.class, InvoiceController.class, CategoryController.class, KitchenController.class})
@Import({SpringSecurityConfig.class, CustomAuthenticationEntryPoint.class, CustomAccessDenied.class, JacksonConfig.class})
class SpringSecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private DishService dishService;

    @MockitoBean
    private InvoiceService invoiceService;

    @MockitoBean
    private CategoryService categoryService;

    @MockitoBean
    private OrderService orderService;

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
        mockMvc.perform(get("/api/kitchen/queue"))
                .andExpect(status().isOk());
    }
}