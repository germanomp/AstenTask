package com.astentask.service;

import com.astentask.dtos.TimeLogCreateDTO;
import com.astentask.dtos.TimeLogDTO;
import com.astentask.exception.ResourceNotFoundException;
import com.astentask.model.Task;
import com.astentask.model.TimeLog;
import com.astentask.model.User;
import com.astentask.repositories.TaskRepository;
import com.astentask.repositories.TimeLogRepository;
import com.astentask.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import jakarta.validation.Valid;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TimeLogService {

    private final TimeLogRepository timeLogRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public Page<TimeLogDTO> listTimeLogs(Long taskId, int page, int size, String sortBy, String direction,
                                         Optional<Long> userIdFilter,
                                         Optional<LocalDateTime> startDateFilter,
                                         Optional<LocalDateTime> endDateFilter) {
        Pageable pageable = PageRequest.of(page, size,
                "desc".equalsIgnoreCase(direction) ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending());

        Specification<TimeLog> spec = (root, query, cb) -> cb.equal(root.get("task").get("id"), taskId);

        if (userIdFilter.isPresent()) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("user").get("id"), userIdFilter.get()));
        }

        if (startDateFilter.isPresent()) {
            spec = spec.and((root, query, cb) ->
                    cb.greaterThanOrEqualTo(root.get("startTime"), startDateFilter.get()));
        }

        if (endDateFilter.isPresent()) {
            spec = spec.and((root, query, cb) ->
                    cb.lessThanOrEqualTo(root.get("startTime"), endDateFilter.get()));
        }


        Page<TimeLog> pageResult = timeLogRepository.findAll(spec, pageable);

        return pageResult.map(this::toDTO);
    }

    public TimeLogDTO addTimeLog(Long taskId, @Valid TimeLogCreateDTO dto) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task não encontrada"));

        User user = getLoggedUser();

        TimeLog timeLog = TimeLog.builder()
                .task(task)
                .user(user)
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .durationInMinutes(dto.getDurationInMinutes())
                .build();

        TimeLog saved = timeLogRepository.save(timeLog);
        log.info("TimeLog criado: id={} para taskId={}", saved.getId(), taskId);
        return toDTO(saved);
    }

    public TimeLogDTO updateTimeLog(Long id, @Valid TimeLogCreateDTO dto) {
        TimeLog timeLog = timeLogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Registro de tempo não encontrado"));

        timeLog.setStartTime(dto.getStartTime());
        timeLog.setEndTime(dto.getEndTime());
        timeLog.setDurationInMinutes(dto.getDurationInMinutes());

        TimeLog updated = timeLogRepository.save(timeLog);
        log.info("TimeLog atualizado: id={}", updated.getId());
        return toDTO(updated);
    }

    public void deleteTimeLog(Long id) {
        TimeLog timeLog = timeLogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Registro de tempo não encontrado"));
        timeLogRepository.delete(timeLog);
        log.info("TimeLog deletado: id={}", id);
    }

    private User getLoggedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário logado não encontrado"));
    }

    private TimeLogDTO toDTO(TimeLog timeLog) {
        return TimeLogDTO.builder()
                .id(timeLog.getId())
                .taskId(timeLog.getTask().getId())
                .userId(timeLog.getUser().getId())
                .startTime(timeLog.getStartTime())
                .endTime(timeLog.getEndTime())
                .durationInMinutes(timeLog.getDurationInMinutes())
                .createdAt(timeLog.getCreatedAt())
                .updatedAt(timeLog.getUpdatedAt())
                .build();
    }
}
