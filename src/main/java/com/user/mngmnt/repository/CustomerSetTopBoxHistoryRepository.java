package com.user.mngmnt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.user.mngmnt.enums.SetTopBoxStatus;
import com.user.mngmnt.model.CustomerSetTopBox;
import com.user.mngmnt.model.CustomerSetTopBoxHistory;

@Repository
public interface CustomerSetTopBoxHistoryRepository extends JpaRepository<CustomerSetTopBoxHistory, Long> {

	CustomerSetTopBoxHistory findByCustomerSetTopBoxAndSetTopBoxStatusAndIsReActivated(CustomerSetTopBox customerSetTopBox, SetTopBoxStatus setTopBoxStatus, boolean isReActivated);

	
}
