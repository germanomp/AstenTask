package com.astentask.controller;

import com.astentask.dtos.TaskAttachmentDTO;
import com.astentask.model.TaskAttachment;
import com.astentask.service.TaskAttachmentService;
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
public class TaskAttachmentController {

    private final TaskAttachmentService attachmentService;

    @PostMapping
    public ResponseEntity<TaskAttachmentDTO> uploadAttachment(@PathVariable Long taskId,
                                                              @RequestParam("file") MultipartFile file) throws IOException {
        TaskAttachmentDTO dto = attachmentService.saveAttachment(taskId, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @GetMapping
    public ResponseEntity<List<TaskAttachmentDTO>> listAttachments(@PathVariable Long taskId) {
        List<TaskAttachmentDTO> attachments = attachmentService.getAttachmentsByTaskId(taskId);
        return ResponseEntity.ok(attachments);
    }

    @GetMapping("/{attachmentId}")
    public ResponseEntity<byte[]> downloadAttachment(@PathVariable Long attachmentId) {
        TaskAttachment attachment = attachmentService.getAttachment(attachmentId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + attachment.getFilename() + "\"")
                .contentType(MediaType.parseMediaType(attachment.getFileType()))
                .body(attachment.getData());
    }

    @DeleteMapping("/{attachmentId}")
    public ResponseEntity<Void> deleteAttachment(@PathVariable Long taskId, @PathVariable Long attachmentId) {
        attachmentService.deleteAttachment(attachmentId);
        return ResponseEntity.noContent().build();
    }
}