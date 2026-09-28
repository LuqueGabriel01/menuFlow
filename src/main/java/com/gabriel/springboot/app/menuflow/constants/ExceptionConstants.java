package com.gabriel.springboot.app.menuflow.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExceptionConstants {
    public static final String USER_ENABLED_MESSAGE = "User already enabled";
    public static final String USER_DISABLED_MESSAGE = "User already disabled";
    public static final String INVALID_INVOICE_MESSAGE = "Invoice is null";
    public static final String INVALID_PRICE_MESSAGE = "Price must be greater than zero";
    public static final String USER_NOT_FOUND_MESSAGE = "User not found";
    public static final String INVALID_CREDENTIALS_MESSAGE = "Invalid username or password";
    public static final String TABLE_NOT_FOUND_MESSAGE = "Table not found";
    public static final String TABLE_NOT_AVAILABLE_MESSAGE = "Table not available";
    public static final String VALIDATION_ERROR_MESSAGE = "Validation error";
    public static final String AUTHENTICATION_REQUIRED_MESSAGE = "Authentication required";
    public static final String INTERNAL_ERROR_MESSAGE = "Internal error.";
    public static final String DINING_TABLE_EXISTS_MESSAGE = "dining table already exists ";
    public static final String TABLE_ACTIVE_MESSAGE = "table has active session";
    public static final String TABLE_NUMBER_NOT_NULL_MESSAGE = "Table number cannot be null";
    public static final String DELETE_SUCCESS_MESSAGE = "Table deleted successfully";

    public static final String CATEGORY_NOT_FOUND_MESSAGE = "Category not found";
    public static final String CATEGORY_EXISTS_MESSAGE = "Category already exists: ";
    public static final String CATEGORY_HAS_DISHES_MESSAGE = "Category has associated dishes";
    public static final String CATEGORY_DELETE_SUCCESS_MESSAGE = "Category deleted successfully";

    public static final String INGREDIENT_NOT_FOUND_MESSAGE = "Ingredient not found";
    public static final String INGREDIENT_EXISTS_MESSAGE = "Ingredient already exists: ";
    public static final String INGREDIENT_DELETE_SUCCESS_MESSAGE = "Ingredient deleted successfully";

    public static final String ALLERGEN_NOT_FOUND_MESSAGE = "Allergen not found";
    public static final String ALLERGEN_EXISTS_MESSAGE = "Allergen already exists: ";
    public static final String ALLERGEN_DELETE_SUCCESS_MESSAGE = "Allergen deleted successfully";

    public static final String DISH_NOT_FOUND_MESSAGE = "Dish not found";
    public static final String DISH_DELETE_SUCCESS_MESSAGE = "Dish deleted successfully";

    public static final String SESSION_NOT_FOUND_MESSAGE = "Session not found";
    public static final String SESSION_NOT_ACTIVE_MESSAGE = "Session is not active";

    public static final String ORDER_NOT_FOUND_MESSAGE = "Order not found";
    public static final String ORDER_ITEM_NOT_FOUND_MESSAGE = "Order item not found";
    public static final String ORDER_NOT_MODIFIABLE_MESSAGE = "Order can no longer be modified";
    public static final String ORDER_ALREADY_CLOSED_MESSAGE = "Order is already closed or cancelled";
    public static final String INVALID_ORDER_STATUS_MESSAGE = "Invalid status transition";
    public static final String DISH_NOT_AVAILABLE_MESSAGE = "Dish is not available";

    public static final String INVOICE_NOT_FOUND_MESSAGE = "Invoice not found";
    public static final String INVOICE_ALREADY_EXISTS_MESSAGE = "This order has already been invoiced";
    public static final String ORDER_NOT_READY_FOR_INVOICE_MESSAGE = "Order must be served before it can be invoiced";
}
