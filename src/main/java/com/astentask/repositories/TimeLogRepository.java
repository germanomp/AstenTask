package com.astentask.repositories;

import com.astentask.model.TimeLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TimeLogRepository extends JpaRepository<TimeLog, Long>, JpaSpecificationExecutor<TimeLog> {
}