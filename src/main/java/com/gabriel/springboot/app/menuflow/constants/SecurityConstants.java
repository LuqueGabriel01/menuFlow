package com.gabriel.springboot.app.menuflow.constants;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class SecurityConstants {

    // --- JWT Claims Keys ---
    public static final String AUTHORITIES_KEY = "authorities";

    // --- HTTP Headers ---
    public static final String HEADER_AUTHORIZATION = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";

    // --- Public api ---
    public static final String LOGIN_URL = "/api/auth/login";
    public static final String REGISTER_URL = "/api/auth/register";

    // --- CORS configuration ---
    public static final long MAX_AGE_SECS = 3600;
}
