package com.user.mngmnt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.user.mngmnt.model.CustomerLedgre;

@Repository
public interface CustomerLedgreRepository extends JpaRepository<CustomerLedgre, Long> {
	
}
