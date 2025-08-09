package com.astentask.service;

import com.astentask.dtos.AuthResponseDTO;
import com.astentask.dtos.LoginRequestDTO;
import com.astentask.dtos.RefreshTokenRequestDTO;
import com.astentask.dtos.RegisterRequestDTO;

public interface AuthService {
    AuthResponseDTO register(RegisterRequestDTO request);
    AuthResponseDTO login(LoginRequestDTO request);
    AuthResponseDTO refreshToken(RefreshTokenRequestDTO request);
    void logout(RefreshTokenRequestDTO request);
}
