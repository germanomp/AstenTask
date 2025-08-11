package com.astentask.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
public class ErrorResponseDTO {

    @Schema(description = "Código HTTP do erro", example = "404")
    private int status;

    @Schema(description = "Mensagem de erro detalhada", example = "Usuário não encontrado")
    private String message;

    @Schema(description = "Timestamp ISO-8601 do erro", example = "2000-01-01T00:00:00Z")
    private Instant timestamp;
}
