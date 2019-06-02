package com.user.mngmnt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.user.mngmnt.model.CustomerType;


@Repository
public interface CustomerTypeRepository extends JpaRepository<CustomerType, Long> {

	CustomerType findByCustomerType(String customerType);
	
}
