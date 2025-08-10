package com.astentask.service;

import com.astentask.dtos.DashboardOverviewDTO;
import com.astentask.dtos.ProjectReportDTO;
import com.astentask.dtos.TaskSummaryDTO;
import com.astentask.exception.ResourceNotFoundException;
import com.astentask.model.*;
import com.astentask.repositories.TaskRepository;
import com.astentask.repositories.TimeLogRepository;
import com.astentask.repositories.UserRepository;
import com.astentask.repositories.ProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardService {

    private final TaskRepository taskRepository;
    private final TimeLogRepository timeLogRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;

    public DashboardOverviewDTO getOverview(Long userId) {
        Long totalTasks = taskRepository.countByAssigneeId(userId);
        Long openTasks = taskRepository.countByAssigneeIdAndStatus(userId, TaskStatus.PENDING);
        Long completedTasks = taskRepository.countByAssigneeIdAndStatus(userId, TaskStatus.COMPLETED);

        Long totalTimeLogged = timeLogRepository.sumDurationByUserId(userId).orElse(0L);

        return DashboardOverviewDTO.builder()
                .totalTasks(totalTasks)
                .openTasks(openTasks)
                .completedTasks(completedTasks)
                .totalTimeLoggedMinutes(totalTimeLogged)
                .build();
    }

    public Page<TaskSummaryDTO> getMyTasks(Long userId, int page, int size, String sortBy, String direction,
                                           Optional<String> statusFilter, Optional<String> priorityFilter,
                                           Optional<LocalDateTime> dueDateStart, Optional<LocalDateTime> dueDateEnd) {
        Pageable pageable = PageRequest.of(page, size,
                "desc".equalsIgnoreCase(direction) ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending());

        Specification<Task> spec = (root, query, cb) -> cb.equal(root.get("assignee").get("id"), userId);

        if (statusFilter.isPresent()) {
            try {
                TaskStatus statusEnum = TaskStatus.valueOf(statusFilter.get().toUpperCase());
                spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), statusEnum));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Status inválido: " + statusFilter.get());
            }
        }
        if (priorityFilter.isPresent()) {
            try {
                TaskPriority priorityEnum = TaskPriority.valueOf(priorityFilter.get().toUpperCase());
                spec = spec.and((root, query, cb) -> cb.equal(root.get("priority"), priorityEnum));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Prioridade inválida: " + priorityFilter.get());
            }
        }
        if (dueDateStart.isPresent()) {
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("dueDate"), dueDateStart.get()));
        }
        if (dueDateEnd.isPresent()) {
            spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("dueDate"), dueDateEnd.get()));
        }

        return taskRepository.findAll(spec, pageable)
                .map(task -> TaskSummaryDTO.builder()
                        .id(task.getId())
                        .title(task.getTitle())
                        .status(task.getStatus())
                        .priority(task.getPriority())
                        .dueDate(task.getDueDate())
                        .build());
    }


    public ProjectReportDTO getProjectReport(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Projeto não encontrado"));

        Long totalTasks = taskRepository.countByProjectId(projectId);
        Long completedTasks = taskRepository.countByProjectIdAndStatus(projectId, TaskStatus.COMPLETED);
        Long openTasks = taskRepository.countByProjectIdAndStatus(projectId, TaskStatus.PENDING);


        Long totalTimeLogged = timeLogRepository.sumDurationByProjectId(projectId).orElse(0L);

        return ProjectReportDTO.builder()
                .projectId(projectId)
                .projectName(project.getName())
                .totalTasks(totalTasks)
                .completedTasks(completedTasks)
                .openTasks(openTasks)
                .totalTimeLoggedMinutes(totalTimeLogged)
                .build();
    }

    public User getLoggedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário logado não encontrado"));
    }
}
