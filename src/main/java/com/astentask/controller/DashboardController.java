package com.astentask.controller;

import com.astentask.dtos.DashboardOverviewDTO;
import com.astentask.dtos.ProjectReportDTO;
import com.astentask.dtos.TaskSummaryDTO;
import com.astentask.model.User;
import com.astentask.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/overview")
    public ResponseEntity<DashboardOverviewDTO> getOverview() {
        User user = dashboardService.getLoggedUser();
        DashboardOverviewDTO overview = dashboardService.getOverview(user.getId());
        return ResponseEntity.ok(overview);
    }

    @GetMapping("/my-tasks")
    public ResponseEntity<Page<TaskSummaryDTO>> getMyTasks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "dueDate") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String priority,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dueDateStart,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dueDateEnd
    ) {
        User user = dashboardService.getLoggedUser();
        Page<TaskSummaryDTO> tasks = dashboardService.getMyTasks(
                user.getId(),
                page,
                size,
                sortBy,
                direction,
                Optional.ofNullable(status),
                Optional.ofNullable(priority),
                Optional.ofNullable(dueDateStart),
                Optional.ofNullable(dueDateEnd)
        );
        return ResponseEntity.ok(tasks);
    }


    @GetMapping("/reports/project/{projectId}")
    public ResponseEntity<ProjectReportDTO> getProjectReport(@PathVariable Long projectId) {
        ProjectReportDTO report = dashboardService.getProjectReport(projectId);
        return ResponseEntity.ok(report);
    }
}
