package com.astentask.service.impl;

import com.astentask.dtos.*;
import com.astentask.exception.ResourceNotFoundException;
import com.astentask.model.Role;
import com.astentask.model.User;
import com.astentask.repositories.UserRepository;
import com.astentask.security.JwtUtil;
import com.astentask.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    //teste
    private final Map<String, String> refreshTokenStore = new ConcurrentHashMap<>();

    @Override
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email já está em uso.");
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.VIEWER)
                .build();

        userRepository.save(user);

        String accessToken = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
        String refreshToken = jwtUtil.generateToken(user.getEmail(), user.getRole().name());

        refreshTokenStore.put(user.getEmail(), refreshToken);

        return new AuthResponse(accessToken, refreshToken);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Credenciais inválidas");
        }

        String accessToken = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
        String refreshToken = jwtUtil.generateToken(user.getEmail(), user.getRole().name());

        refreshTokenStore.put(user.getEmail(), refreshToken);

        return new AuthResponse(accessToken, refreshToken);
    }

    @Override
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        String oldToken = request.getRefreshToken();
        if (!jwtUtil.isTokenValid(oldToken)) {
            throw new RuntimeException("Refresh token inválido ou expirado");
        }

        String email = jwtUtil.extractEmail(oldToken);
        String savedRefresh = refreshTokenStore.get(email);

        if (!oldToken.equals(savedRefresh)) {
            throw new RuntimeException("Refresh token não autorizado");
        }

        String newAccessToken = jwtUtil.generateToken(email, jwtUtil.extractRole(oldToken));
        String newRefreshToken = jwtUtil.generateToken(email, jwtUtil.extractRole(oldToken));

        refreshTokenStore.put(email, newRefreshToken);

        return new AuthResponse(newAccessToken, newRefreshToken);
    }

    @Override
    public void logout(RefreshTokenRequest request) {
        String email = jwtUtil.extractEmail(request.getRefreshToken());
        refreshTokenStore.remove(email);
    }
}
