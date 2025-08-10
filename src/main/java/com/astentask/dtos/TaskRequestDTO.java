package com.astentask.dtos;

import com.astentask.model.TaskPriority;
import com.astentask.model.TaskStatus;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskRequestDTO {

    @NotBlank(message = "Título é obrigatório")
    private String title;
    @Size(max = 500, message = "Descrição deve ter até 500 caracteres")
    private String description;
    @NotNull(message = "Status é obrigatório")
    private TaskStatus status;
    @NotNull(message = "Prioridade é obrigatória")
    private TaskPriority priority;
    @FutureOrPresent(message = "Data de vencimento deve ser hoje ou no futuro")
    private LocalDateTime dueDate;

    private Long assigneeId;
}
