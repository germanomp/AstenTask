package com.astentask.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProjectReportDTO {
    private Long projectId;
    private String projectName;
    private Long totalTasks;
    private Long completedTasks;
    private Long openTasks;
    private Long totalTimeLoggedMinutes;
}
