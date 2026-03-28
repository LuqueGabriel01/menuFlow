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
}
