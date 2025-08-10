package com.astentask.service;
import com.astentask.dtos.CommentCreateDTO;
import com.astentask.dtos.CommentDTO;
import com.astentask.exception.ResourceNotFoundException;
import com.astentask.model.Comment;
import com.astentask.model.Task;
import com.astentask.model.User;
import com.astentask.repositories.CommentRepository;
import com.astentask.repositories.TaskRepository;
import com.astentask.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {

    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public Page<CommentDTO> listComments(Long taskId, int page, int size, String sortBy, String direction) {
        Pageable pageable = PageRequest.of(page, size,
                "desc".equalsIgnoreCase(direction) ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending());

        Page<Comment> commentsPage = commentRepository.findByTaskId(taskId, pageable);

        return commentsPage.map(this::toDTO);
    }

    public CommentDTO addComment(Long taskId, CommentCreateDTO dto) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task não encontrada"));

        User author = getLoggedUser();

        Comment comment = Comment.builder()
                .content(dto.getContent())
                .task(task)
                .author(author)
                .build();

        Comment saved = commentRepository.save(comment);

        return toDTO(saved);
    }

    public CommentDTO updateComment(Long id, CommentCreateDTO dto) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comentário não encontrado"));

        comment.setContent(dto.getContent());

        Comment updated = commentRepository.save(comment);
        return toDTO(updated);
    }

    public void deleteComment(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comentário não encontrado"));

        commentRepository.delete(comment);
    }

    private User getLoggedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário logado não encontrado"));
    }

    private CommentDTO toDTO(Comment comment) {
        return CommentDTO.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .taskId(comment.getTask().getId())
                .authorId(comment.getAuthor().getId())
                .build();
    }
}