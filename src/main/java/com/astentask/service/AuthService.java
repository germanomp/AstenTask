package com.astentask.service;

import com.astentask.dtos.*;
import com.astentask.exception.EmailAlreadyUsedException;
import com.astentask.exception.ResourceNotFoundException;
import com.astentask.model.Role;
import com.astentask.model.User;
import com.astentask.repositories.UserRepository;
import com.astentask.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    private final Map<String, String> refreshTokenStore = new ConcurrentHashMap<>();

    public MessageResponseDTO register(RegisterRequestDTO request) {
        log.info("Registrando usuário");
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new EmailAlreadyUsedException("Email já está em uso.");
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.VIEWER)
                .build();

        userRepository.save(user);

        return new MessageResponseDTO("Registro realizado com sucesso!");
    }

    public AuthResponseDTO login(LoginRequestDTO request) {
        log.info("Usuário fazendo login");
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Credenciais inválidas");
        }

        String accessToken = jwtUtil.generateAccessToken(user.getEmail(), user.getRole().name());
        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail(), user.getRole().name());

        refreshTokenStore.put(user.getEmail(), refreshToken);

        return new AuthResponseDTO(accessToken, refreshToken);
    }

    public AuthResponseDTO refreshToken(RefreshTokenRequestDTO request) {
        String oldRefreshToken = request.getRefreshToken();

        if (!jwtUtil.isTokenValid(oldRefreshToken)) {
            throw new RuntimeException("Refresh token inválido ou expirado");
        }

        if (jwtUtil.extractTokenType(oldRefreshToken) != JwtUtil.TokenType.REFRESH) {
            throw new RuntimeException("Token inválido para refresh");
        }

        String email = jwtUtil.extractEmail(oldRefreshToken);
        String savedRefreshToken = refreshTokenStore.get(email);

        if (!oldRefreshToken.equals(savedRefreshToken)) {
            throw new RuntimeException("Refresh token não autorizado");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        String newAccessToken = jwtUtil.generateAccessToken(user.getEmail(), user.getRole().name());
        String newRefreshToken = jwtUtil.generateRefreshToken(user.getEmail(), user.getRole().name());

        refreshTokenStore.put(email, newRefreshToken);

        return new AuthResponseDTO(newAccessToken, newRefreshToken);
    }

    public void logout(RefreshTokenRequestDTO request) {
        log.info("Usuário fazendo logout");
        String email = jwtUtil.extractEmail(request.getRefreshToken());
        refreshTokenStore.remove(email);
    }
}
