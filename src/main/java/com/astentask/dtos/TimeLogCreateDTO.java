package com.astentask.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimeLogCreateDTO {
    @NotNull
    private LocalDateTime startTime;

    private LocalDateTime endTime;

    @Min(1)
    private Integer durationInMinutes;
}
