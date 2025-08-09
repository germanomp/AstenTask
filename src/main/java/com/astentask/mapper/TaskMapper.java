package com.astentask.mapper;

import com.astentask.dtos.TaskRequestDTO;
import com.astentask.dtos.TaskResponseDTO;
import com.astentask.model.Task;
import com.astentask.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TaskMapper {

    public TaskResponseDTO toDTO(Task task) {
        return TaskResponseDTO.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .priority(task.getPriority())
                .assigneeId(task.getAssignee() != null ? task.getAssignee().getId() : null)
                .assigneeName(task.getAssignee() != null ? task.getAssignee().getName() : null)
                .projectId(task.getProject() != null ? task.getProject().getId() : null)
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .dueDate(task.getDueDate())
                .build();
    }

    public Task toEntity(TaskRequestDTO dto, User assignee) {
        Task task = new Task();
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setStatus(dto.getStatus());
        task.setPriority(dto.getPriority());
        task.setDueDate(dto.getDueDate());
        task.setAssignee(assignee);
        return task;
    }

    public void updateEntity(Task task, TaskRequestDTO dto, User assignee) {
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setStatus(dto.getStatus());
        task.setPriority(dto.getPriority());
        task.setDueDate(dto.getDueDate());
        task.setAssignee(assignee);
    }
}
