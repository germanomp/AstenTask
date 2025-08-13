package com.astentask.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MessageResponseDTO {
    @Schema(description = "Mensagem de criação de usuário", example = "Usuário registrado com sucesso!")
    private String message;
}