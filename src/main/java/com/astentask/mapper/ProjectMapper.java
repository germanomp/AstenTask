package com.astentask.mapper;

import com.astentask.dtos.ProjectRequestDTO;
import com.astentask.dtos.ProjectResponseDTO;
import com.astentask.dtos.UserResponseDTO;
import com.astentask.model.Project;
import com.astentask.model.User;

public class ProjectMapper {

    public static ProjectResponseDTO toDto(Project project) {
        User owner = project.getOwner();

        UserResponseDTO ownerDTO = UserResponseDTO.builder()
                .id(owner.getId())
                .name(owner.getName())
                .email(owner.getEmail())
                .role(owner.getRole().name())
                .build();

        return ProjectResponseDTO.builder()
                .id(project.getId())
                .name(project.getName())
                .description(project.getDescription())
                .createdAt(project.getCreatedAt())
                .updatedAt(project.getUpdatedAt())
                .owner(ownerDTO)
                .build();
    }

    public static Project toEntity(ProjectRequestDTO dto) {
        return Project.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .build();
    }
}
