package com.astentask.controller;

import com.astentask.dtos.CommentCreateDTO;
import com.astentask.dtos.CommentDTO;
import com.astentask.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/tasks/{taskId}/comments")
    public ResponseEntity<Page<CommentDTO>> listComments(
            @PathVariable Long taskId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {

        return ResponseEntity.ok(commentService.listComments(taskId, page, size, sortBy, direction));
    }

    @PostMapping("/tasks/{taskId}/comments")
    public ResponseEntity<CommentDTO> addComment(
            @PathVariable Long taskId,
            @Valid @RequestBody CommentCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(commentService.addComment(taskId, dto));
    }

    @PutMapping("/comments/{id}")
    public ResponseEntity<CommentDTO> updateComment(
            @PathVariable Long id,
            @Valid @RequestBody CommentCreateDTO dto) {
        return ResponseEntity.ok(commentService.updateComment(id, dto));
    }

    @DeleteMapping("/comments/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
        return ResponseEntity.noContent().build();
    }
}
