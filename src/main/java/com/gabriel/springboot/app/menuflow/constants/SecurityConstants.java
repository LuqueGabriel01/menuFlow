package com.gabriel.springboot.app.menuflow.constants;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class SecurityConstants {

    public static final String TOKEN_REQUIRED_MESSAGE = "{\"success\":false,\"message\":\"Not authenticated. Token required.\"}";
    public static final String ACCESS_DENIED_MESSAGE = "{\"success\":false,\"message\":\"Access denied. Insufficient permissions.\"}";

    // --- JWT Claims Keys ---
    public static final String AUTHORITIES_KEY = "authorities";

    // --- HTTP Headers ---
    public static final String HEADER_AUTHORIZATION = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String APPLICATION_JSON = "application/json";

    // --- Public api ---
    public static final String ANY_PATH = "/**";
    public static final String ALL_RESOURCES = "*";
    public static final String API_AUTH = "/api/auth/**";
    public static final String SWAGGER_UI_PATH = "/swagger-ui/**";
    public static final String API_DOCS_PATH = "/v3/api-docs/**";
    public static final String LOGIN_URL = "/api/auth/login";
    public static final String REGISTER_URL = "/api/auth/register";
    public static final String DISHES_ANY_PATH = "/api/dishes/**";
    public static final String CATEGORIES_ANY_PATH = "/api/categories/**";
    public static final String DINING_TABLES_ANY_PATH = "/api/dining-tables/**";
    public static final String SESSION_ANY_PATH = "/api/sessions/**";
    public static final String KITCHEN_ANY_PATH = "/api/kitchen/**";
    public static final String INVOICE_ANY_PATH = "/api/invoices/**";

    // --- Roles
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_CASHIER = "CASHIER";
    public static final String ROLE_CHEF = "CHEF";
    public static final String ROLE_KITCHEN = "KITCHEN";

    public static final String HTTP_GET = "GET";
    public static final String HTTP_POST = "POST";
    public static final String HTTP_PUT = "PUT";
    public static final String HTTP_DELETE = "DELETE";
    public static final String HTTP_OPTIONS = "OPTIONS";

    // --- CORS configuration ---
    public static final long MAX_AGE_SECS = 3600;
}
