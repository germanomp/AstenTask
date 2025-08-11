package com.astentask.controller;

import com.astentask.dtos.ErrorResponseDTO;
import com.astentask.dtos.TaskAttachmentDTO;
import com.astentask.model.TaskAttachment;
import com.astentask.service.TaskAttachmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/tasks/{taskId}/attachments")
@RequiredArgsConstructor
@Tag(name = "Anexos de Tarefas", description = "Gerenciamento de anexos (arquivos) vinculados às tarefas.")
public class TaskAttachmentController {

    private final TaskAttachmentService attachmentService;

    @Operation(
            summary = "Enviar anexo para tarefa",
            description = "Faz upload de um arquivo e associa como anexo à tarefa especificada.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Anexo criado com sucesso",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = TaskAttachmentDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Arquivo inválido ou erro no upload",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class))),
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
    @PostMapping
    public ResponseEntity<TaskAttachmentDTO> uploadAttachment(
            @Parameter(description = "ID da tarefa para anexar o arquivo", example = "1")
            @PathVariable Long taskId,
            @Parameter(description = "Arquivo a ser enviado", required = true)
            @RequestParam("file") MultipartFile file) throws IOException {
        TaskAttachmentDTO dto = attachmentService.saveAttachment(taskId, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @Operation(
            summary = "Listar anexos da tarefa",
            description = "Retorna a lista de anexos associados à tarefa especificada.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de anexos retornada com sucesso",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = TaskAttachmentDTO.class))),
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
    @GetMapping
    public ResponseEntity<List<TaskAttachmentDTO>> listAttachments(
            @Parameter(description = "ID da tarefa para listar anexos", example = "1")
            @PathVariable Long taskId) {
        List<TaskAttachmentDTO> attachments = attachmentService.getAttachmentsByTaskId(taskId);
        return ResponseEntity.ok(attachments);
    }

    @Operation(
            summary = "Baixar anexo",
            description = "Faz download do arquivo anexo pelo ID do anexo.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Anexo baixado com sucesso",
                            content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE)),
                    @ApiResponse(responseCode = "403", description = "Acesso negado",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Anexo não encontrado",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class))),
                    @ApiResponse(responseCode = "500", description = "Erro interno do servidor",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class)))
            }
    )
    @GetMapping("/{attachmentId}")
    public ResponseEntity<byte[]> downloadAttachment(
            @Parameter(description = "ID do anexo para download", example = "1")
            @PathVariable Long attachmentId) {
        TaskAttachment attachment = attachmentService.getAttachment(attachmentId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + attachment.getFilename() + "\"")
                .contentType(MediaType.parseMediaType(attachment.getFileType()))
                .body(attachment.getData());
    }

    @Operation(
            summary = "Excluir anexo",
            description = "Remove um anexo associado à tarefa.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Anexo excluído com sucesso"),
                    @ApiResponse(responseCode = "403", description = "Acesso negado",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Anexo não encontrado",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class))),
                    @ApiResponse(responseCode = "500", description = "Erro interno do servidor",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseDTO.class)))
            }
    )
    @DeleteMapping("/{attachmentId}")
    public ResponseEntity<Void> deleteAttachment(
            @Parameter(description = "ID da tarefa", example = "1")
            @PathVariable Long taskId,
            @Parameter(description = "ID do anexo a ser excluído", example = "1")
            @PathVariable Long attachmentId) {
        attachmentService.deleteAttachment(attachmentId);
        return ResponseEntity.noContent().build();
    }
}
