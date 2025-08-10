package com.astentask.controller;

import com.astentask.dtos.TimeLogCreateDTO;
import com.astentask.dtos.TimeLogDTO;
import com.astentask.service.TimeLogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TimeLogController {

    private final TimeLogService timeLogService;

    @GetMapping("/tasks/{taskId}/timelogs")
    public ResponseEntity<Page<TimeLogDTO>> listTimeLogs(
            @PathVariable Long taskId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "startTime") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            @RequestParam Optional<Long> userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Optional<LocalDateTime> startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Optional<LocalDateTime> endDate
    ) {
        return ResponseEntity.ok(timeLogService.listTimeLogs(taskId, page, size, sortBy, direction, userId, startDate, endDate));
    }

    @PostMapping("/tasks/{taskId}/timelogs")
    public ResponseEntity<TimeLogDTO> addTimeLog(@PathVariable Long taskId,
                                                 @Valid @RequestBody TimeLogCreateDTO dto) {
        return ResponseEntity.status(201).body(timeLogService.addTimeLog(taskId, dto));
    }

    @PutMapping("/timelogs/{id}")
    public ResponseEntity<TimeLogDTO> updateTimeLog(@PathVariable Long id,
                                                    @Valid @RequestBody TimeLogCreateDTO dto) {
        return ResponseEntity.ok(timeLogService.updateTimeLog(id, dto));
    }

    @DeleteMapping("/timelogs/{id}")
    public ResponseEntity<Void> deleteTimeLog(@PathVariable Long id) {
        timeLogService.deleteTimeLog(id);
        return ResponseEntity.noContent().build();
    }
}
