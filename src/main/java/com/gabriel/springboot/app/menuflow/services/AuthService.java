package com.gabriel.springboot.app.menuflow.services;

import com.gabriel.springboot.app.menuflow.models.dto.request.LoginRequest;
import com.gabriel.springboot.app.menuflow.models.dto.request.TableLoginRequest;
import com.gabriel.springboot.app.menuflow.models.dto.response.AuthResponse;

public interface AuthService {

    AuthResponse login(LoginRequest request);
    AuthResponse loginTable(TableLoginRequest request);
}
