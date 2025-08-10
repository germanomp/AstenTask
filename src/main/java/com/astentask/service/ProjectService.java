package com.astentask.service;

import com.astentask.dtos.ProjectRequestDTO;
import com.astentask.dtos.ProjectResponseDTO;
import com.astentask.dtos.ProjectStatsDTO;
import com.astentask.exception.ResourceNotFoundException;
import com.astentask.mapper.ProjectMapper;
import com.astentask.model.Project;
import com.astentask.model.User;
import com.astentask.repositories.ProjectRepository;
import com.astentask.repositories.TaskRepository;
import com.astentask.specification.ProjectSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;

    @Transactional(readOnly = true)
    public Page<ProjectResponseDTO> listProjectByUser(
            User user,
            String name,
            LocalDateTime startDate,
            LocalDateTime endDate,
            int page,
            int size,
            String sortBy,
            String direction) {

        log.info("Listando projetos do usuário {} com filtros name={}, startDate={}, endDate={}", user.getEmail(), name, startDate, endDate);

        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Specification<Project> spec = (root, query, cb) -> cb.conjunction();

        if (user != null) {
            spec = spec.and(ProjectSpecification.hasOwner(user));
        }
        if (name != null && !name.isEmpty()) {
            spec = spec.and(ProjectSpecification.hasNameLike(name));
        }
        if (startDate != null && endDate != null) {
            spec = spec.and(ProjectSpecification.createdBetween(startDate, endDate));
        }

        Page<Project> projectsPage = projectRepository.findAll(spec, pageable);

        return projectsPage.map(ProjectMapper::toDto);
    }

    @Transactional(readOnly = true)
    public ProjectResponseDTO getProjectByIdAndUser(Long ProjectId, User user) {
        Project project = projectRepository.findById(ProjectId)
                .filter(p -> p.getOwner().getId().equals(user.getId()))
                .orElseThrow(() -> new ResourceNotFoundException("Projeto não encontrado!"));

        return ProjectMapper.toDto(project);
    }

    @Transactional
    public ProjectResponseDTO createProject(ProjectRequestDTO dto, User user) {
        Project project = ProjectMapper.toEntity(dto);
        project.setOwner(user);
        project = projectRepository.save(project);
        log.info("Projeto criado com id {}", project.getId());
        return ProjectMapper.toDto(project);
    }

    @Transactional
    public ProjectResponseDTO updateProject(Long id, ProjectRequestDTO dto, User user) {
        Project project = projectRepository.findById(id)
                .filter(p -> p.getOwner().getId().equals(user.getId()))
                .orElseThrow(() -> new ResourceNotFoundException("Projeto não encontrado!"));

        project.setName(dto.getName());
        project.setDescription(dto.getDescription());
        project = projectRepository.save(project);

        log.info("Projeto atualizado id {}", id);
        return ProjectMapper.toDto(project);
    }

    @Transactional
    public void deleteProject(Long id, User user) {
        Project project = projectRepository.findById(id)
                .filter(p -> p.getOwner().getId().equals(user.getId()))
                .orElseThrow(() -> new ResourceNotFoundException("Projeto não encontrado!"));

        projectRepository.delete(project);
        log.info("Projeto deletado id {}", id);
    }

    public ProjectStatsDTO getProjectStats(Long projectId) {
        long total = taskRepository.countByProjectId(projectId);

        Map<String, Long> byStatus = taskRepository.countTasksGroupedByStatus(projectId);

        Map<String, Long> byPriority = taskRepository.countTasksGroupedByPriority(projectId);

        long doneCount = byStatus.getOrDefault("DONE", 0L);
        double completionPercentage = total > 0 ? (doneCount * 100.0) / total : 0.0;

        return new ProjectStatsDTO(
                projectId,
                total,
                byStatus,
                byPriority,
                completionPercentage
        );
    }
}
