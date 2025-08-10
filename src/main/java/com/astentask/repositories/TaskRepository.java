package com.astentask.repositories;

import com.astentask.model.Task;
import com.astentask.model.TaskStatus;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public interface TaskRepository extends JpaRepository<Task, Long>, JpaSpecificationExecutor<Task> {

    long countByProjectId(Long projectId);

    long countByAssigneeId(Long assigneeId);

    long countByAssigneeIdAndStatus(Long assigneeId, TaskStatus status);

    long countByProjectIdAndStatus(Long projectId, TaskStatus status);

    @Query("SELECT t.status, COUNT(t) FROM Task t WHERE t.project.id = :projectId GROUP BY t.status")
    List<Object[]> countTasksGroupedByStatusRaw(@Param("projectId") Long projectId);

    @Query("SELECT t.priority, COUNT(t) FROM Task t WHERE t.project.id = :projectId GROUP BY t.priority")
    List<Object[]> countTasksGroupedByPriorityRaw(@Param("projectId") Long projectId);

    default Map<String, Long> countTasksGroupedByStatus(Long projectId) {
        return countTasksGroupedByStatusRaw(projectId).stream()
                .collect(Collectors.toMap(
                        row -> row[0].toString(),
                        row -> (Long) row[1]
                ));
    }

    default Map<String, Long> countTasksGroupedByPriority(Long projectId) {
        return countTasksGroupedByPriorityRaw(projectId).stream()
                .collect(Collectors.toMap(
                        row -> row[0].toString(),
                        row -> (Long) row[1]
                ));
    }
}
