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
