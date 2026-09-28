package com.gabriel.springboot.app.menuflow.services;

import com.gabriel.springboot.app.menuflow.models.dto.request.auth.LoginRequest;
import com.gabriel.springboot.app.menuflow.models.dto.request.auth.RegisterRequest;
import com.gabriel.springboot.app.menuflow.models.dto.request.table.TableLoginRequest;
import com.gabriel.springboot.app.menuflow.models.dto.response.auth.AuthResponse;

public interface AuthService {

    AuthResponse login(LoginRequest request);
    AuthResponse loginTable(TableLoginRequest request);
    void register (RegisterRequest request);
}
