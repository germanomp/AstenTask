package com.astentask.controller;

import com.astentask.dtos.DashboardOverviewDTO;
import com.astentask.dtos.ErrorResponseDTO;
import com.astentask.dtos.ProjectReportDTO;
import com.astentask.dtos.TaskSummaryDTO;
import com.astentask.model.User;
import com.astentask.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "Endpoints para obter informações resumidas e relatórios do dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    @Operation(
            summary = "Visão geral do dashboard",
            description = "Retorna uma visão geral dos dados relevantes para o usuário logado.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Visão geral retornada com sucesso",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = DashboardOverviewDTO.class))),
                    @ApiResponse(responseCode = "403", description = "Acesso negado",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class))),
                    @ApiResponse(responseCode = "500", description = "Erro interno do servidor",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class)))
            }
    )
    @GetMapping("/overview")
    public ResponseEntity<DashboardOverviewDTO> getOverview() {
        User user = dashboardService.getLoggedUser();
        DashboardOverviewDTO overview = dashboardService.getOverview(user.getId());
        return ResponseEntity.ok(overview);
    }

    @Operation(
            summary = "Listar minhas tarefas",
            description = "Retorna uma lista paginada das tarefas do usuário logado, com filtros opcionais por status, prioridade e intervalo de datas de vencimento.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de tarefas retornada com sucesso",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = TaskSummaryDTO.class))),
                    @ApiResponse(responseCode = "403", description = "Acesso negado",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class))),
                    @ApiResponse(responseCode = "500", description = "Erro interno do servidor",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class)))
            }
    )
    @GetMapping("/my-tasks")
    public ResponseEntity<Page<TaskSummaryDTO>> getMyTasks(
            @Parameter(description = "Número da página (0-index)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamanho da página") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Campo para ordenação") @RequestParam(defaultValue = "dueDate") String sortBy,
            @Parameter(description = "Direção da ordenação (asc/desc)") @RequestParam(defaultValue = "asc") String direction,
            @Parameter(description = "Filtro por status da tarefa") @RequestParam(required = false) String status,
            @Parameter(description = "Filtro por prioridade da tarefa") @RequestParam(required = false) String priority,
            @Parameter(description = "Data inicial para filtro de data de vencimento (ISO 8601)", example = "2025-08-01T00:00:00") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dueDateStart,
            @Parameter(description = "Data final para filtro de data de vencimento (ISO 8601)", example = "2025-08-31T23:59:59") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dueDateEnd
    ) {
        User user = dashboardService.getLoggedUser();
        Page<TaskSummaryDTO> tasks = dashboardService.getMyTasks(
                user.getId(),
                page,
                size,
                sortBy,
                direction,
                Optional.ofNullable(status),
                Optional.ofNullable(priority),
                Optional.ofNullable(dueDateStart),
                Optional.ofNullable(dueDateEnd)
        );
        return ResponseEntity.ok(tasks);
    }

    @Operation(
            summary = "Relatório do projeto",
            description = "Retorna o relatório detalhado de um projeto específico.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Relatório do projeto retornado com sucesso",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ProjectReportDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Projeto não encontrado",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class))),
                    @ApiResponse(responseCode = "403", description = "Acesso negado",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class))),
                    @ApiResponse(responseCode = "500", description = "Erro interno do servidor",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class)))
            }
    )
    @GetMapping("/reports/project/{projectId}")
    public ResponseEntity<ProjectReportDTO> getProjectReport(
            @Parameter(description = "ID do projeto") @PathVariable Long projectId) {
        ProjectReportDTO report = dashboardService.getProjectReport(projectId);
        return ResponseEntity.ok(report);
    }
}
