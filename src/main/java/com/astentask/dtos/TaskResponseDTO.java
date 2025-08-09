package com.astentask.dtos;

import com.astentask.model.TaskPriority;
import com.astentask.model.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskResponseDTO {
    private Long id;
    private String title;
    private String description;
    private TaskStatus status;
    private TaskPriority priority;
    private LocalDateTime dueDate;
    private Long assigneeId;
    private String assigneeName;
    private Long projectId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
