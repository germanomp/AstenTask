package com.astentask.controller;

import com.astentask.dtos.TaskRequestDTO;
import com.astentask.dtos.TaskResponseDTO;
import com.astentask.model.TaskPriority;
import com.astentask.model.TaskStatus;
import com.astentask.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @GetMapping("/projects/{projectId}/tasks")
    public Page<TaskResponseDTO> listTasks(
            @PathVariable Long projectId,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) TaskPriority priority,
            @RequestParam(required = false) Long assigneeId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startCreated,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endCreated,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        Sort sort = direction.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        return taskService.listTasks(projectId, title, status, priority,
                assigneeId, startCreated, endCreated, pageable);
    }

    @GetMapping("/tasks/{id}")
    public TaskResponseDTO getTask(@PathVariable Long id) {
        return taskService.getTaskById(id);
    }

    @PostMapping("/projects/{projectId}/tasks")
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponseDTO createTask(@PathVariable Long projectId,
                                      @RequestBody @Valid TaskRequestDTO dto) {
        return taskService.createTask(projectId, dto);
    }

    @PutMapping("/tasks/{id}")
    public TaskResponseDTO updateTask(@PathVariable Long id,
                                      @RequestBody @Valid TaskRequestDTO dto) {
        return taskService.updateTask(id, dto);
    }

    @DeleteMapping("/tasks/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
    }

    @PutMapping("/tasks/{id}/status")
    public TaskResponseDTO updateStatus(@PathVariable Long id,
                                        @RequestParam TaskStatus status) {
        return taskService.updateStatus(id, status);
    }

    @PutMapping("/tasks/{id}/assign")
    public TaskResponseDTO assignUser(@PathVariable Long id,
                                      @RequestParam Long userId) {
        return taskService.assignUser(id, userId);
    }
}
