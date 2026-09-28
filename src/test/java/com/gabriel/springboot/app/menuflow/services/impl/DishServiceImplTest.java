package com.gabriel.springboot.app.menuflow.services.impl;

import com.gabriel.springboot.app.menuflow.exceptions.ResourceNotFoundException;
import com.gabriel.springboot.app.menuflow.mappers.DishMapper;
import com.gabriel.springboot.app.menuflow.models.dto.request.product.CreateDishRequest;
import com.gabriel.springboot.app.menuflow.models.dto.request.product.UpdateDishRequest;
import com.gabriel.springboot.app.menuflow.models.dto.response.product.DishResponse;
import com.gabriel.springboot.app.menuflow.models.entities.Category;
import com.gabriel.springboot.app.menuflow.models.entities.Dish;
import com.gabriel.springboot.app.menuflow.models.entities.User;
import com.gabriel.springboot.app.menuflow.repositories.AllergenRepository;
import com.gabriel.springboot.app.menuflow.repositories.CategoryRepository;
import com.gabriel.springboot.app.menuflow.repositories.DishRepository;
import com.gabriel.springboot.app.menuflow.repositories.IngredientRepository;
import com.gabriel.springboot.app.menuflow.repositories.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.gabriel.springboot.app.menuflow.constants.LocaleConstants.DEFAULT_LANG;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DishServiceImplTest {

    @Mock
    private DishRepository dishRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private IngredientRepository ingredientRepository;

    @Mock
    private AllergenRepository allergenRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private DishMapper dishMapper;

    @InjectMocks
    private DishServiceImpl dishService;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Should create dish successfully when category exists")
    void createDish() {
        CreateDishRequest request = new CreateDishRequest(1L, "Pizza", "Cheese and tomato pizza", BigDecimal.TEN, true, null, null);

        Category category = Category.of();
        User user = User.of("chef", "chef@menuflow.com");
        Dish dish = Dish.of(category, BigDecimal.TEN, user);
        DishResponse response = new DishResponse(1L, 1L, "Mains", "Pizza", "Cheese and tomato pizza",
                BigDecimal.TEN, true, null, null, null, "chef", Collections.emptyList(), Collections.emptyList());

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("chef", null, List.of()));

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(userRepository.findByUsername("chef")).thenReturn(Optional.of(user));
        when(dishRepository.save(any(Dish.class))).thenReturn(dish);
        when(dishMapper.toResponse(dish, DEFAULT_LANG)).thenReturn(response);

        DishResponse result = dishService.createDish(request);

        assertNotNull(result);
        verify(dishRepository).save(any(Dish.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when creating dish with unknown category")
    void createDishCategoryNotFound() {
        CreateDishRequest request = new CreateDishRequest(1L, "Pizza", "Cheese and tomato pizza", BigDecimal.TEN, true, null, null);

        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> dishService.createDish(request));

        verify(dishRepository, never()).save(any(Dish.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when dish does not exist")
    void getDishByIdNotFound() {
        when(dishRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> dishService.getDishById(1L));
    }

    @Test
    @DisplayName("Should update dish successfully")
    void updateDish() {
        UpdateDishRequest request = new UpdateDishRequest(1L, "Pizza", "Updated description here", BigDecimal.valueOf(12), true, null, null);

        Category category = Category.of();
        setId(category, Category.class, 1L);
        Dish dish = Dish.of(category, BigDecimal.TEN, null);
        DishResponse response = new DishResponse(1L, 1L, "Mains", "Pizza", "Updated description here",
                BigDecimal.valueOf(12), true, null, null, null, null, Collections.emptyList(), Collections.emptyList());

        when(dishRepository.findById(1L)).thenReturn(Optional.of(dish));
        when(dishRepository.save(dish)).thenReturn(dish);
        when(dishMapper.toResponse(dish, DEFAULT_LANG)).thenReturn(response);

        DishResponse result = dishService.updateDish(1L, request);

        assertNotNull(result);
        assertEquals(BigDecimal.valueOf(12), dish.getPrice());
    }

    @Test
    @DisplayName("Should return list of all dishes")
    void getAllDishes() {
        when(dishRepository.findAllByLanguage(DEFAULT_LANG)).thenReturn(List.of());

        List<DishResponse> result = dishService.getAllDishes();

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should return list of available dishes")
    void getAvailableDishes() {
        when(dishRepository.findByAvailableTrue()).thenReturn(List.of());

        List<DishResponse> result = dishService.getAvailableDishes();

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should delete dish successfully")
    void deleteDish() {
        Category category = Category.of();
        Dish dish = Dish.of(category, BigDecimal.TEN, null);

        when(dishRepository.findById(1L)).thenReturn(Optional.of(dish));

        dishService.deleteDishById(1L);

        verify(dishRepository).delete(dish);
    }

    private void setId(Object entity, Class<?> declaringClass, Long id) {
        try {
            java.lang.reflect.Field field = declaringClass.getDeclaredField("id");
            field.setAccessible(true);
            field.set(entity, id);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }
}
