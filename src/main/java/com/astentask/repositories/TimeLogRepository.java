package com.astentask.repositories;

import com.astentask.model.TimeLog;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface TimeLogRepository extends JpaRepository<TimeLog, Long>, JpaSpecificationExecutor<TimeLog> {

    @Query("SELECT SUM(t.durationInMinutes) FROM TimeLog t WHERE t.user.id = :userId")
    Optional<Long> sumDurationByUserId(@Param("userId") Long userId);

    @Query("SELECT SUM(t.durationInMinutes) FROM TimeLog t WHERE t.task.project.id = :projectId")
    Optional<Long> sumDurationByProjectId(@Param("projectId") Long projectId);

}