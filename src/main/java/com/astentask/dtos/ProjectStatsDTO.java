package com.astentask.dtos;

import java.util.Map;

public record ProjectStatsDTO(
        Long projectId,
        long totalTasks,
        Map<String, Long> tasksByStatus,
        Map<String, Long> tasksByPriority,
        double completionPercentage
) {

}
