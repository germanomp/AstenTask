package com.astentask.specification;

import com.astentask.model.Task;
import com.astentask.model.TaskPriority;
import com.astentask.model.TaskStatus;
import com.astentask.model.User;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class TaskSpecification {

    public static Specification<Task> hasTitle(String title) {
        return (root, query, cb) ->
                title == null ? null :
                        cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%");
    }

    public static Specification<Task> hasStatus(TaskStatus status) {
        return (root, query, cb) ->
                status == null ? null :
                        cb.equal(root.get("status"), status);
    }

    public static Specification<Task> hasPriority(TaskPriority priority) {
        return (root, query, cb) ->
                priority == null ? null :
                        cb.equal(root.get("priority"), priority);
    }

    public static Specification<Task> assignedTo(User assignee) {
        return (root, query, cb) ->
                assignee == null ? null :
                        cb.equal(root.get("assignee"), assignee);
    }

    public static Specification<Task> belongsToProject(Long projectId) {
        return (root, query, cb) ->
                projectId == null ? null :
                        cb.equal(root.get("project").get("id"), projectId);
    }

    public static Specification<Task> dueDateBetween(LocalDateTime start, LocalDateTime end) {
        return (root, query, cb) -> {
            if (start != null && end != null) {
                return cb.between(root.get("dueDate"), start, end);
            }
            return null;
        };
    }

    public static Specification<Task> createdBetween(LocalDateTime start, LocalDateTime end) {
        return (root, query, cb) -> {
            if (start != null && end != null) {
                return cb.between(root.get("createdAt"), start, end);
            }
            return null;
        };
    }
}
