package com.user.mngmnt.repository;

import com.user.mngmnt.model.Area;
import com.user.mngmnt.model.PlanChangeControl;
import com.user.mngmnt.model.PlanChangeControlStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface PlanChangeControlRepository extends JpaRepository<PlanChangeControl, Long> {

    @Modifying
    @Query("update PlanChangeControl p set p.status='IN_PROGRESS', p.executionId=:executionId where p.status='NOT_PROCESSED'")
    @Transactional
    int markPlanChangeControlToExecute(Long executionId);

    @Modifying
    @Query("update PlanChangeControl p set p.status=:newStatus where p.status=:existingStatus and p.executionId=:executionId")
    @Transactional
    int updatePlanChangeControlStatus(Long executionId, PlanChangeControlStatus existingStatus, PlanChangeControlStatus newStatus);

    @Query("select p from PlanChangeControl p where p.executionId=:executionId")
    List<PlanChangeControl> findNotProcessedRecords(Long executionId);

}
