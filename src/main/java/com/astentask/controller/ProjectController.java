package com.astentask.controller;

import com.astentask.dtos.ProjectRequestDTO;
import com.astentask.dtos.ProjectResponseDTO;
import com.astentask.dtos.ProjectStatsDTO;
import com.astentask.dtos.ErrorResponseDTO;
import com.astentask.dtos.pages.PagedResponseDTO;
import com.astentask.dtos.pages.PagedResponseDTOProjectResponseDTO;
import com.astentask.model.User;
import com.astentask.repositories.UserRepository;
import com.astentask.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
@Tag(name = "Projects", description = "Gerenciamento de projetos do usuário autenticado")
public class ProjectController {

    private final ProjectService projectService;
    private final UserRepository userRepository;

    @Operation(
            summary = "Listar projetos do usuário autenticado",
            description = "Retorna uma lista paginada de projetos, podendo filtrar por nome e intervalo de datas.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de projetos retornada com sucesso",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = PagedResponseDTOProjectResponseDTO.class))),
                    @ApiResponse(responseCode = "403", description = "Acesso negado",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class))),
                    @ApiResponse(responseCode = "500", description = "Erro interno no servidor",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class)))
            })
    @GetMapping
    public PagedResponseDTO<ProjectResponseDTO> getProjectsByUser(
            Authentication authentication,
            @Parameter(description = "Filtrar por nome do projeto") @RequestParam(required = false) String name,
            @Parameter(description = "Data inicial de criação") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "Data final de criação") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @Parameter(description = "Página atual (0-index)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamanho da página") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Campo de ordenação") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Direção da ordenação (asc/desc)") @RequestParam(defaultValue = "desc") String direction
    ) {
        User user = getUserFromAuth(authentication);

        Sort sort = direction.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ProjectResponseDTO> projectPage = projectService.listProjectByUser(user, name, startDate, endDate, page, size, sortBy, direction);

        return new PagedResponseDTO<>(
                projectPage.getContent(),
                projectPage.getNumber(),
                projectPage.getSize(),
                projectPage.getTotalElements(),
                projectPage.getTotalPages(),
                projectPage.isLast()
        );
    }

    @Operation(
            summary = "Buscar projeto por ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Projeto encontrado",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProjectResponseDTO.class))),
                    @ApiResponse(responseCode = "403", description = "Acesso negado",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Projeto não encontrado",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))),
                    @ApiResponse(responseCode = "500", description = "Erro interno no servidor",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class)))
            }
    )
    @GetMapping("/{id}")
    public ProjectResponseDTO getProjectById(@PathVariable Long id, Authentication authentication) {
        User user = getUserFromAuth(authentication);
        return projectService.getProjectByIdAndUser(id, user);
    }

    @Operation(
            summary = "Criar novo projeto",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Projeto criado com sucesso",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProjectResponseDTO.class))),
                    @ApiResponse(responseCode = "403", description = "Acesso negado",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Dados inválidos",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))),
                    @ApiResponse(responseCode = "500", description = "Erro interno no servidor",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class)))
            }
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectResponseDTO createProject(@RequestBody @Valid ProjectRequestDTO dto, Authentication authentication) {
        User user = getUserFromAuth(authentication);
        return projectService.createProject(dto, user);
    }

    @Operation(
            summary = "Atualizar projeto",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Projeto atualizado com sucesso",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProjectResponseDTO.class))),
                    @ApiResponse(responseCode = "403", description = "Acesso negado",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Projeto não encontrado",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))),
                    @ApiResponse(responseCode = "500", description = "Erro interno no servidor",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class)))
            }
    )
    @PutMapping("/{id}")
    public ProjectResponseDTO updateProject(@PathVariable Long id, @RequestBody @Valid ProjectRequestDTO dto, Authentication authentication) {
        User user = getUserFromAuth(authentication);
        return projectService.updateProject(id, dto, user);
    }

    @Operation(
            summary = "Excluir projeto",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Projeto excluído com sucesso"),
                    @ApiResponse(responseCode = "404", description = "Projeto não encontrado",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))),
                    @ApiResponse(responseCode = "403", description = "Acesso negado",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class))),
                    @ApiResponse(responseCode = "500", description = "Erro interno no servidor",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class)))
            }
    )
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProject(@PathVariable Long id, Authentication authentication) {
        User user = getUserFromAuth(authentication);
        projectService.deleteProject(id, user);
    }

    @Operation(
            summary = "Obter estatísticas do projeto",
            description = "Retorna estatísticas de tarefas filtradas por status, prioridade, responsável e datas.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Estatísticas retornadas com sucesso",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProjectStatsDTO.class))),
                    @ApiResponse(responseCode = "403", description = "Acesso negado",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Projeto não encontrado",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))),
                    @ApiResponse(responseCode = "500", description = "Erro interno no servidor",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class)))
            }
    )
    @GetMapping("/{id}/stats")
    public ProjectStatsDTO getStats(
            @PathVariable Long id,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String priority,
            @RequestParam(required = false) Long assigneeId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startCreated,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endCreated,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        Sort sort = direction.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        return projectService.getProjectStatsFiltered(id, status, priority, assigneeId, startCreated, endCreated, pageable);
    }

    private User getUserFromAuth(Authentication authentication) {
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado!"));
    }
}
