package com.astentask.specification;

import com.astentask.model.Project;
import com.astentask.model.User;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class ProjectSpecification {

    public static Specification<Project> hasOwner(User owner) {
        return (root, query, cb) -> owner == null ? null : cb.equal(root.get("owner"), owner);
    }

    public static Specification<Project> hasNameLike(String name) {
        return (root, query, cb) -> name == null ? null : cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<Project> createdBetween(LocalDateTime start, LocalDateTime end) {
        return (root, query, cb) -> {
            if (start != null && end != null) {
                return cb.between(root.get("createdAt"), start, end);
            }
            return null;
        };
    }
}
