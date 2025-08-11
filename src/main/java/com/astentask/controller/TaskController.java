package com.astentask.controller;

import com.astentask.dtos.ErrorResponseDTO;
import com.astentask.dtos.TaskRequestDTO;
import com.astentask.dtos.TaskResponseDTO;
import com.astentask.dtos.pages.PagedResponseDTO;
import com.astentask.model.TaskPriority;
import com.astentask.model.TaskStatus;
import com.astentask.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Tarefas", description = "Gerenciamento de tarefas dos projetos.")
public class TaskController {

    private final TaskService taskService;

    @Operation(summary = "Listar tarefas de um projeto",
            description = "Retorna uma lista paginada de tarefas filtrando opcionalmente por título, status, prioridade, responsável e intervalo de datas.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de tarefas retornada com sucesso",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = PagedResponseDTO.class))),
                    @ApiResponse(responseCode = "403", description = "Acesso negado",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class))),
                    @ApiResponse(responseCode = "500", description = "Erro interno do servidor",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class)))
            })
    @GetMapping("/projects/{projectId}/tasks")
    public ResponseEntity<PagedResponseDTO<TaskResponseDTO>> listTasks(
            @Parameter(description = "ID do projeto") @PathVariable Long projectId,
            @Parameter(description = "Filtrar por título") @RequestParam(required = false) String title,
            @Parameter(description = "Filtrar por status") @RequestParam(required = false) TaskStatus status,
            @Parameter(description = "Filtrar por prioridade") @RequestParam(required = false) TaskPriority priority,
            @Parameter(description = "Filtrar por ID do responsável") @RequestParam(required = false) Long assigneeId,
            @Parameter(description = "Data inicial de criação (ISO)") @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startCreated,
            @Parameter(description = "Data final de criação (ISO)") @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endCreated,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        Sort sort = direction.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<TaskResponseDTO> resultPage = taskService.listTasks(projectId, title, status, priority, assigneeId, startCreated, endCreated, pageable);

        return ResponseEntity.ok(new PagedResponseDTO<>(
                resultPage.getContent(),
                resultPage.getNumber(),
                resultPage.getSize(),
                resultPage.getTotalElements(),
                resultPage.getTotalPages(),
                resultPage.isLast()
        ));
    }

    @Operation(summary = "Buscar tarefa por ID",
            description = "Retorna os detalhes de uma tarefa específica.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Tarefa encontrada",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = TaskResponseDTO.class))),
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
    @GetMapping("/tasks/{id}")
    public ResponseEntity<TaskResponseDTO> getTask(@Parameter(description = "ID da tarefa") @PathVariable Long id) {
        return ResponseEntity.ok(taskService.getTaskById(id));
    }

    @Operation(summary = "Criar tarefa",
            description = "Cria uma nova tarefa em um projeto.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Tarefa criada com sucesso",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = TaskResponseDTO.class))),
                    @ApiResponse(responseCode = "403", description = "Acesso negado",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class))),
                    @ApiResponse(responseCode = "500", description = "Erro interno do servidor",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class)))
            })
    @PostMapping("/projects/{projectId}/tasks")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<TaskResponseDTO> createTask(
            @Parameter(description = "ID do projeto") @PathVariable Long projectId,
            @Valid @RequestBody TaskRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(taskService.createTask(projectId, dto));
    }

    @Operation(summary = "Atualizar tarefa",
            description = "Atualiza os dados de uma tarefa existente.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Tarefa atualizada com sucesso",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = TaskResponseDTO.class))),
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
    @PutMapping("/tasks/{id}")
    public ResponseEntity<TaskResponseDTO> updateTask(
            @Parameter(description = "ID da tarefa") @PathVariable Long id,
            @Valid @RequestBody TaskRequestDTO dto) {
        return ResponseEntity.ok(taskService.updateTask(id, dto));
    }

    @Operation(summary = "Excluir tarefa",
            description = "Remove uma tarefa do sistema.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Tarefa excluída com sucesso"),
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
    @DeleteMapping("/tasks/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteTask(@Parameter(description = "ID da tarefa") @PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Atualizar status da tarefa",
            description = "Atualiza o status de uma tarefa.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Status atualizado com sucesso",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = TaskResponseDTO.class))),
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
    @PutMapping("/tasks/{id}/status")
    public ResponseEntity<TaskResponseDTO> updateStatus(
            @Parameter(description = "ID da tarefa") @PathVariable Long id,
            @Parameter(description = "Novo status da tarefa") @RequestParam TaskStatus status) {
        return ResponseEntity.ok(taskService.updateStatus(id, status));
    }

    @Operation(summary = "Atribuir usuário a tarefa",
            description = "Define um responsável por uma tarefa.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Usuário atribuído com sucesso",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = TaskResponseDTO.class))),
                    @ApiResponse(responseCode = "403", description = "Acesso negado",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Tarefa ou usuário não encontrado",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class))),
                    @ApiResponse(responseCode = "500", description = "Erro interno do servidor",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class)))
            })
    @PutMapping("/tasks/{id}/assign")
    public ResponseEntity<TaskResponseDTO> assignUser(
            @Parameter(description = "ID da tarefa") @PathVariable Long id,
            @Parameter(description = "ID do usuário a ser atribuído") @RequestParam Long userId) {
        return ResponseEntity.ok(taskService.assignUser(id, userId));
    }
}
