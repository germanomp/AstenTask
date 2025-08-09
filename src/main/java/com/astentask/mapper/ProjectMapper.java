package com.astentask.mapper;

import com.astentask.dtos.ProjectResponseDTO;
import com.astentask.model.Project;

public class ProjectMapper {

    public static ProjectResponseDTO toDTO(Project project) {
        return ProjectResponseDTO.builder()
                .id(project.getId())
                .name(project.getName())
                .description(project.getDescription())
                .createdAt(project.getCreatedAt())
                .updatedAt(project.getUpdatedAt())
                .owner(project.getOwner())
                .build();
    }

    public static Project toEntity(ProjectResponseDTO dto) {
        return Project.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .build();
    }
}
