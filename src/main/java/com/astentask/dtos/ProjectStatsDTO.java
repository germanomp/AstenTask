package com.astentask.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.Map;

@Data
@AllArgsConstructor
public class ProjectStatsDTO {
    private Long projectId;
    private long totalTasks;
    private Map<String, Long> tasksByStatus;
    private Map<String, Long> tasksByPriority;
    private double completionPercentage;
    private Page<TaskResponseDTO> filteredTasks;
}
