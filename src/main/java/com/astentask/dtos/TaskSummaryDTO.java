package com.astentask.dtos;

import com.astentask.model.TaskPriority;
import com.astentask.model.TaskStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class TaskSummaryDTO {
    private Long id;
    private String title;
    private TaskStatus status;
    private TaskPriority priority;
    private LocalDateTime dueDate;
}
