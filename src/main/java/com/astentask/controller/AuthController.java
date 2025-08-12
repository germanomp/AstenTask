package com.astentask.controller;

import com.astentask.dtos.*;
import com.astentask.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticação", description = "Endpoints para autenticação e gerenciamento de tokens")
public class AuthController {

    private final AuthService authService;

    @Operation(
            summary = "Registrar novo usuário",
            description = "Registra um novo usuário no sistema.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Registro realizado com sucesso",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = AuthResponseDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Dados inválidos no registro",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class))),
                    @ApiResponse(responseCode = "400", description = "E-mail em uso",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class))),
                    @ApiResponse(responseCode = "500", description = "Erro interno do servidor",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class)))
            }
    )
    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(
            @Parameter(description = "Dados para registro do usuário") @Valid @RequestBody RegisterRequestDTO request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @Operation(
            summary = "Login de usuário",
            description = "Realiza o login do usuário e retorna token de autenticação.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Login realizado com sucesso",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = AuthResponseDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Dados inválidos no login",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class))),
                    @ApiResponse(responseCode = "500", description = "Erro interno do servidor",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class)))
            }
    )
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(
            @Parameter(description = "Dados para login do usuário") @Valid @RequestBody LoginRequestDTO request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @Operation(
            summary = "Atualizar token JWT",
            description = "Atualiza o token de acesso usando o refresh token fornecido.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Token atualizado com sucesso",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = AuthResponseDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Refresh token inválido ou não autorizado",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class))),
                    @ApiResponse(responseCode = "500", description = "Erro interno do servidor",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class)))
            }
    )
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponseDTO> refreshToken(
            @Parameter(description = "Refresh token para atualização") @RequestBody RefreshTokenRequestDTO request) {
        return ResponseEntity.ok(authService.refreshToken(request));
    }

    @Operation(
            summary = "Logout do usuário",
            description = "Realiza logout invalidando o refresh token fornecido.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Logout realizado com sucesso"),
                    @ApiResponse(responseCode = "400", description = "Refresh token inválido",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class))),
                    @ApiResponse(responseCode = "500", description = "Erro interno do servidor",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class)))
            }
    )
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @Parameter(description = "Refresh token para logout") @RequestBody RefreshTokenRequestDTO request) {
        authService.logout(request);
        return ResponseEntity.ok().build();
    }
}
