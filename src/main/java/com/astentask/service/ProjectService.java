package com.astentask.service;

import com.astentask.dtos.ProjectRequestDTO;
import com.astentask.dtos.ProjectResponseDTO;
import com.astentask.exception.ResourceNotFoundException;
import com.astentask.mapper.ProjectMapper;
import com.astentask.model.Project;
import com.astentask.model.User;
import com.astentask.repositories.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;

    @Transactional(readOnly = true)
    public List<ProjectResponseDTO> listProjectByUser(User user) {
        List<Project> projects = projectRepository.findByOwner(user);
        return projects.stream()
                .map(ProjectMapper::toDTO)
                .collect(Collectors.toList());
    }

    public ProjectResponseDTO getProjectByIdAndUser(Long ProjectId, User user) {
        Project project = projectRepository.findById(ProjectId)
                .filter(p -> p.getOwner().getId().equals(user.getId()))
                .orElseThrow(() -> new ResourceNotFoundException("Projeto não encontrado!"));

        return ProjectMapper.toDTO(project);
    }

    public ProjectResponseDTO createProject(ProjectRequestDTO dto, User user) {
        Project project = ProjectMapper.toEntity(dto);
        project.setOwner(user);
        project = projectRepository.save(project);
        return ProjectMapper.toDTO(project);
    }

    public ProjectResponseDTO updateProject(Long id, ProjectRequestDTO dto, User user) {
        Project project = projectRepository.findById(id)
                .filter(p -> p.getOwner().getId().equals(user.getId()))
                .orElseThrow(() -> new ResourceNotFoundException("Projeto não encontrado!"));

        project.setName(dto.getName());
        project.setDescription(dto.getDescription());
        project = projectRepository.save(project);

        return ProjectMapper.toDTO(project);
    }

    public void deleteProject(Long id, User user) {
        Project project = projectRepository.findById(id)
                .filter(p -> p.getOwner().getId().equals(user.getId()))
                .orElseThrow(() -> new ResourceNotFoundException("Projeto não encontrado!"));

        projectRepository.delete(project);
    }
}
