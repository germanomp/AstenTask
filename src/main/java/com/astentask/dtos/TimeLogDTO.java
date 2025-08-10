package com.astentask.dtos;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimeLogDTO {
    private Long id;
    private Long taskId;
    private Long userId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer durationInMinutes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}