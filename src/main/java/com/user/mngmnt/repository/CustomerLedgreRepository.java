package com.user.mngmnt.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.user.mngmnt.enums.CustomerLedgreEntry;
import com.user.mngmnt.model.CustomerLedgre;

@Repository
public interface CustomerLedgreRepository extends JpaRepository<CustomerLedgre, Long> {

	Page<CustomerLedgre> findByCustomerLedgreEntry(CustomerLedgreEntry customerLedgreEntry, Pageable pageRequest);

	long countByCustomerLedgreEntry(CustomerLedgreEntry customerLedgreEntry);

}
