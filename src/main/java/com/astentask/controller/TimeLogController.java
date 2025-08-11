package com.astentask.controller;

import com.astentask.dtos.ErrorResponseDTO;
import com.astentask.dtos.TimeLogCreateDTO;
import com.astentask.dtos.TimeLogDTO;
import com.astentask.dtos.pages.PagedResponseDTOTimeLogDTO;
import com.astentask.service.TimeLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Registros de Tempo", description = "Gerenciamento de registros de tempo (TimeLogs) vinculados às tarefas.")
public class TimeLogController {

    private final TimeLogService timeLogService;

    @Operation(summary = "Listar registros de tempo",
            description = "Retorna uma lista paginada de registros de tempo de uma tarefa, com filtros opcionais por usuário e intervalo de datas.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de registros de tempo retornada com sucesso",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = PagedResponseDTOTimeLogDTO.class))),
                    @ApiResponse(responseCode = "403", description = "Acesso negado",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Tarefa não encontrada",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class))),
                    @ApiResponse(responseCode = "500", description = "Erro interno do servidor",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class)))
            })
    @GetMapping("/tasks/{taskId}/timelogs")
    public ResponseEntity<Page<TimeLogDTO>> listTimeLogs(
            @Parameter(description = "ID da tarefa", example = "1") @PathVariable Long taskId,
            @Parameter(description = "Número da página", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamanho da página", example = "10") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Campo para ordenação", example = "startTime") @RequestParam(defaultValue = "startTime") String sortBy,
            @Parameter(description = "Direção da ordenação (asc/desc)", example = "asc") @RequestParam(defaultValue = "asc") String direction,
            @Parameter(description = "Filtrar por ID de usuário") @RequestParam Optional<Long> userId,
            @Parameter(description = "Data inicial (formato ISO)") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Optional<LocalDateTime> startDate,
            @Parameter(description = "Data final (formato ISO)") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Optional<LocalDateTime> endDate
    ) {
        return ResponseEntity.ok(timeLogService.listTimeLogs(taskId, page, size, sortBy, direction, userId, startDate, endDate));
    }

    @Operation(summary = "Adicionar registro de tempo",
            description = "Adiciona um novo registro de tempo a uma tarefa.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Registro de tempo criado com sucesso",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = TimeLogDTO.class))),
                    @ApiResponse(responseCode = "403", description = "Acesso negado",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Tarefa não encontrada",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class))),
                    @ApiResponse(responseCode = "500", description = "Erro interno do servidor",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class)))
            })
    @PostMapping("/tasks/{taskId}/timelogs")
    public ResponseEntity<TimeLogDTO> addTimeLog(
            @Parameter(description = "ID da tarefa", example = "1") @PathVariable Long taskId,
            @Valid @RequestBody TimeLogCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(timeLogService.addTimeLog(taskId, dto));
    }

    @Operation(summary = "Atualizar registro de tempo",
            description = "Atualiza um registro de tempo existente.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Registro de tempo atualizado com sucesso",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = TimeLogDTO.class))),
                    @ApiResponse(responseCode = "403", description = "Acesso negado",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Registro de tempo não encontrado",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class))),
                    @ApiResponse(responseCode = "500", description = "Erro interno do servidor",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class)))
            })
    @PutMapping("/timelogs/{id}")
    public ResponseEntity<TimeLogDTO> updateTimeLog(
            @Parameter(description = "ID do registro de tempo", example = "1") @PathVariable Long id,
            @Valid @RequestBody TimeLogCreateDTO dto) {
        return ResponseEntity.ok(timeLogService.updateTimeLog(id, dto));
    }

    @Operation(summary = "Excluir registro de tempo",
            description = "Remove um registro de tempo do sistema.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Registro de tempo excluído com sucesso"),
                    @ApiResponse(responseCode = "403", description = "Acesso negado",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Registro de tempo não encontrado",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class))),
                    @ApiResponse(responseCode = "500", description = "Erro interno do servidor",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class)))
            })
    @DeleteMapping("/timelogs/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteTimeLog(
            @Parameter(description = "ID do registro de tempo", example = "1") @PathVariable Long id) {
        timeLogService.deleteTimeLog(id);
        return ResponseEntity.noContent().build();
    }
}
