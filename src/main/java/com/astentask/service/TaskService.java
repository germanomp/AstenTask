package com.astentask.service;

import com.astentask.dtos.TaskRequestDTO;
import com.astentask.dtos.TaskResponseDTO;
import com.astentask.exception.ResourceNotFoundException;
import com.astentask.mapper.TaskMapper;
import com.astentask.model.Project;
import com.astentask.model.Task;
import com.astentask.model.TaskPriority;
import com.astentask.model.TaskStatus;
import com.astentask.model.User;
import com.astentask.repositories.ProjectRepository;
import com.astentask.repositories.TaskRepository;
import com.astentask.repositories.UserRepository;
import com.astentask.specification.TaskSpecification;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final TaskMapper taskMapper;

    @Cacheable(value = "tasksSearch", key = "T(java.util.Objects).hash(#projectId, #title, #status, #priority, #assigneeId, #startCreated, #endCreated, #pageable.pageNumber, #pageable.pageSize, #pageable.sort.toString())")
    public Page<TaskResponseDTO> listTasks(Long projectId, String title, TaskStatus status, TaskPriority priority,
                                           Long assigneeId, LocalDateTime startCreated, LocalDateTime endCreated,
                                           Pageable pageable) {

        Specification<Task> spec = null;

        if (projectId != null) {
            spec = TaskSpecification.belongsToProject(projectId);
        }

        if (title != null && !title.isBlank()) {
            spec = (spec == null) ? TaskSpecification.hasTitle(title) : spec.and(TaskSpecification.hasTitle(title));
        }
        if (status != null) {
            spec = (spec == null) ? TaskSpecification.hasStatus(status) : spec.and(TaskSpecification.hasStatus(status));
        }
        if (priority != null) {
            spec = (spec == null) ? TaskSpecification.hasPriority(priority) : spec.and(TaskSpecification.hasPriority(priority));
        }
        if (assigneeId != null) {
            spec = (spec == null) ? TaskSpecification.hasAssigneeId(assigneeId) : spec.and(TaskSpecification.hasAssigneeId(assigneeId));
        }
        if (startCreated != null && endCreated != null) {
            spec = (spec == null) ? TaskSpecification.createdBetween(startCreated, endCreated) : spec.and(TaskSpecification.createdBetween(startCreated, endCreated));
        }

        Page<Task> tasks = taskRepository.findAll(spec, pageable);
        return tasks.map(taskMapper::toDTO);
    }

    @Cacheable(value = "tasksById", key = "#taskId")
    public TaskResponseDTO getTaskById(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Tarefa não encontrada com id " + taskId));
        return taskMapper.toDTO(task);
    }

    @CacheEvict(value = {"tasksSearch", "tasksById"}, allEntries = true)
    public TaskResponseDTO createTask(Long projectId, TaskRequestDTO dto) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Projeto não encontrado com id " + projectId));

        User assignee = null;
        if (dto.getAssigneeId() != null) {
            assignee = userRepository.findById(dto.getAssigneeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Usuário (assignee) não encontrado com id " + dto.getAssigneeId()));
        }
        Task task = taskMapper.toEntity(dto, assignee);
        task.setProject(project);
        Task saved = taskRepository.save(task);

        return taskMapper.toDTO(saved);
    }

    @CacheEvict(value = {"tasksSearch", "tasksById"}, allEntries = true)
    public TaskResponseDTO updateTask(Long taskId, TaskRequestDTO dto) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Tarefa não encontrada com id " + taskId));

        User assignee = null;
        if (dto.getAssigneeId() != null) {
            assignee = userRepository.findById(dto.getAssigneeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Usuário (assignee) não encontrado com id " + dto.getAssigneeId()));
        }

        taskMapper.updateEntity(task, dto, assignee);
        Task updated = taskRepository.save(task);
        return taskMapper.toDTO(updated);
    }

    @CacheEvict(value = {"tasksSearch", "tasksById"}, allEntries = true)
    public void deleteTask(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Tarefa não encontrada com id " + taskId));
        taskRepository.delete(task);
    }

    @CacheEvict(value = {"tasksSearch", "tasksById"}, allEntries = true)
    public TaskResponseDTO updateStatus(Long taskId, TaskStatus status) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Tarefa não encontrada com id " + taskId));
        task.setStatus(status);
        Task updated = taskRepository.save(task);
        return taskMapper.toDTO(updated);
    }

    @CacheEvict(value = {"tasksSearch", "tasksById"}, allEntries = true)
    public TaskResponseDTO assignUser(Long taskId, Long userId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Tarefa não encontrada com id " + taskId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com id " + userId));
        task.setAssignee(user);
        Task updated = taskRepository.save(task);
        return taskMapper.toDTO(updated);
    }
}
