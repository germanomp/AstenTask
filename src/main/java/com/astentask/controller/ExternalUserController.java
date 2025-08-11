package com.astentask.controller;

import com.astentask.dtos.ErrorResponseDTO;
import com.astentask.service.ExternalUserImportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/external")
@RequiredArgsConstructor
@Tag(name = "Importação Externa", description = "Endpoints para importação de usuários externos")
public class ExternalUserController {

    private final ExternalUserImportService importService;

    @Operation(
            summary = "Importar usuários externos",
            description = "Importa usuários de uma fonte externa (JSONPlaceholder).",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Importação concluída com sucesso",
                            content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE)),
                    @ApiResponse(responseCode = "403", description = "Acesso negado",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class))),
                    @ApiResponse(responseCode = "500", description = "Erro interno do servidor durante a importação",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
            }
    )
    @PostMapping("/import-users")
    public ResponseEntity<String> importUsers() {
        importService.importUsersFromJsonPlaceholder();
        return ResponseEntity.ok("Importação concluída com sucesso!");
    }

}
