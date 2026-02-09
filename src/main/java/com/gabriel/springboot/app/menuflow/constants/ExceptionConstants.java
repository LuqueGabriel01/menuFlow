package com.gabriel.springboot.app.menuflow.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExceptionConstants {
    public static final String USER_ENABLED_MESSAGE = "User already enabled";
    public static final String USER_DISABLED_MESSAGE = "User already disabled";
    public static final String INVALID_INVOICE_MESSAGE = "Invoice is null";
    public static final String INVALID_PRICE_MESSAGE = "Price must be greater than zero";
}
