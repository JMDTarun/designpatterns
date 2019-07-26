package com.user.mngmnt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.user.mngmnt.model.CustomerSetTopBoxHistory;

@Repository
public interface CustomerSetTopBoxHistoryRepository extends JpaRepository<CustomerSetTopBoxHistory, Long> {

}
