package com.user.mngmnt.repository;

import com.user.mngmnt.model.RunnerExecution;
import com.user.mngmnt.model.RunnerExecutionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RunnerExecutionRepository extends JpaRepository<RunnerExecution, Long> {
    
    List<RunnerExecution> findByStatus(RunnerExecutionStatus status);

}
