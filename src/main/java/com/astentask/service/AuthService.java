package com.astentask.service;

import com.astentask.dtos.AuthResponse;
import com.astentask.dtos.LoginRequest;
import com.astentask.dtos.RefreshTokenRequest;
import com.astentask.dtos.RegisterRequest;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    AuthResponse refreshToken(RefreshTokenRequest request);
    void logout(RefreshTokenRequest request);
}
