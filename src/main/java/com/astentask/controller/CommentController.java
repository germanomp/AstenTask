package com.astentask.controller;

import com.astentask.dtos.CommentCreateDTO;
import com.astentask.dtos.CommentDTO;
import com.astentask.dtos.ErrorResponseDTO;
import com.astentask.dtos.pages.PagedResponseDTOCommentDTO;
import com.astentask.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Comentários", description = "Gerenciamento de comentários das tarefas.")
public class CommentController {

    private final CommentService commentService;

    @Operation(
            summary = "Listar comentários da tarefa",
            description = "Retorna uma lista paginada de comentários associados a uma tarefa específica.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de comentários retornada com sucesso",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = PagedResponseDTOCommentDTO.class))),
                    @ApiResponse(responseCode = "403", description = "Acesso negado",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Tarefa não encontrada",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class))),
                    @ApiResponse(responseCode = "500", description = "Erro interno do servidor",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class)))
            }
    )
    @GetMapping("/tasks/{taskId}/comments")
    public ResponseEntity<Page<CommentDTO>> listComments(
            @Parameter(description = "ID da tarefa para listar comentários", example = "1")
            @PathVariable Long taskId,
            @Parameter(description = "Número da página (0-index)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamanho da página", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Campo para ordenação", example = "createdAt")
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Direção da ordenação (asc ou desc)", example = "desc")
            @RequestParam(defaultValue = "desc") String direction) {

        return ResponseEntity.ok(commentService.listComments(taskId, page, size, sortBy, direction));
    }

    @Operation(
            summary = "Adicionar comentário à tarefa",
            description = "Adiciona um novo comentário a uma tarefa específica.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Comentário criado com sucesso",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = CommentDTO.class))),
                    @ApiResponse(responseCode = "403", description = "Acesso negado",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Tarefa não encontrada",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Dados inválidos para criação do comentário",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class))),
                    @ApiResponse(responseCode = "500", description = "Erro interno do servidor",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class)))
            }
    )
    @PostMapping("/tasks/{taskId}/comments")
    public ResponseEntity<CommentDTO> addComment(
            @Parameter(description = "ID da tarefa para adicionar comentário", example = "1")
            @PathVariable Long taskId,
            @Parameter(description = "Dados do comentário a ser criado")
            @Valid @RequestBody CommentCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(commentService.addComment(taskId, dto));
    }

    @Operation(
            summary = "Atualizar comentário",
            description = "Atualiza os dados de um comentário existente.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Comentário atualizado com sucesso",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = CommentDTO.class))),
                    @ApiResponse(responseCode = "403", description = "Acesso negado",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Comentário não encontrado",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Dados inválidos para atualização do comentário",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class))),
                    @ApiResponse(responseCode = "500", description = "Erro interno do servidor",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class)))
            }
    )
    @PutMapping("/comments/{id}")
    public ResponseEntity<CommentDTO> updateComment(
            @Parameter(description = "ID do comentário a ser atualizado", example = "1")
            @PathVariable Long id,
            @Parameter(description = "Dados do comentário para atualização")
            @Valid @RequestBody CommentCreateDTO dto) {
        return ResponseEntity.ok(commentService.updateComment(id, dto));
    }

    @Operation(
            summary = "Excluir comentário",
            description = "Remove um comentário do sistema.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Comentário excluído com sucesso"),
                    @ApiResponse(responseCode = "403", description = "Acesso negado",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Comentário não encontrado",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class))),
                    @ApiResponse(responseCode = "500", description = "Erro interno do servidor",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class)))
            }
    )
    @DeleteMapping("/comments/{id}")
    public ResponseEntity<Void> deleteComment(
            @Parameter(description = "ID do comentário a ser excluído", example = "1")
            @PathVariable Long id) {
        commentService.deleteComment(id);
        return ResponseEntity.noContent().build();
    }
}
