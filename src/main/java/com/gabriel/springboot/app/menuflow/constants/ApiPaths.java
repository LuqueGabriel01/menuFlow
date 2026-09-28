package com.gabriel.springboot.app.menuflow.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ApiPaths {

    public static final String AUTH_PATH = "/api/auth";
    public static final String LOGIN = "/login";
    public static final String REGISTER = "/register";
    public static final String TABLE = "/table";
    public static final String TABLE_PATH = "/api/tables";
    public static final String ACTIVE = "/active";
    public static final String PATH_ID = "/{id}";
    public static final String PATH_QR_CODE = "/qr/{qrCode}";
    public static final String PATH_ID_TOGGLE = "/{id}/toggle";

    public static final String CATEGORY_PATH = "/api/categories";
    public static final String INGREDIENT_PATH = "/api/ingredients";
    public static final String ALLERGEN_PATH = "/api/allergens";
    public static final String DISH_PATH = "/api/dishes";
    public static final String AVAILABLE = "/available";
    public static final String PATH_CATEGORY_ID = "/category/{categoryId}";

    public static final String ORDER_PATH = "/api/orders";
    public static final String PATH_SESSION_ID = "/session/{sessionId}";
    public static final String PATH_ID_STATUS = "/{id}/status";
    public static final String PATH_ID_ITEMS = "/{id}/items";
    public static final String PATH_ID_ITEM_ID = "/{id}/items/{itemId}";
    public static final String PATH_ID_CANCEL = "/{id}/cancel";

    public static final String INVOICE_PATH = "/api/invoices";
    public static final String PATH_ORDER_ID = "/order/{orderId}";

    public static final String KITCHEN_PATH = "/api/kitchen";
    public static final String QUEUE = "/queue";

    public static final class Session {
        public static final String SESSIONS = "/api/sessions";
        public static final String OPEN = "/open";
        public static final String CLOSE = "/close";
        public static final String ACTIVE = "/active";
        public static final String ID = "/{id}";
        public static final String ID_DETAILS = "/{id}/details";
        public static final String TABLE_TABLE_ID = "/table/{tableId}";
        public static final String TABLE_TABLE_ID_ACTIVE = "/table/{tableId}/active";
    }
}
