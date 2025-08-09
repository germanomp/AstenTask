package com.astentask.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ProjectRequestDTO {

    @NotBlank
    private String name;
    private String description;
}
