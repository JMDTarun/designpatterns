package com.user.mngmnt.repository;

import com.user.mngmnt.model.PlanChangeControlStatus;
import com.user.mngmnt.model.RunnerExecution;
import com.user.mngmnt.model.RunnerExecutionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface RunnerExecutionRepository extends JpaRepository<RunnerExecution, Long> {

    @Modifying
    @Query("update RunnerExecution p set p.status=:newStatus where p.status=:existingStatus")
    @Transactional
    int updatePlanChangeControlStatusByStatus(
            @Param("existingStatus") RunnerExecutionStatus existingStatus,
            @Param("newStatus") RunnerExecutionStatus newStatus);
    
    List<RunnerExecution> findByStatus(RunnerExecutionStatus status);

}
