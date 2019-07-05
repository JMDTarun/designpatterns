package com.user.mngmnt.repository;

import java.time.Month;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.user.mngmnt.enums.Action;
import com.user.mngmnt.enums.CustomerLedgreEntry;
import com.user.mngmnt.model.Customer;
import com.user.mngmnt.model.CustomerLedgre;
import com.user.mngmnt.model.CustomerSetTopBox;

@Repository
public interface CustomerLedgreRepository extends JpaRepository<CustomerLedgre, Long> {

	Page<CustomerLedgre> findByCustomerLedgreEntry(CustomerLedgreEntry customerLedgreEntry, Pageable pageRequest);

	long countByCustomerLedgreEntry(CustomerLedgreEntry customerLedgreEntry);

	CustomerLedgre findByCustomerAndCustomerSetTopBoxAndActionAndMonth(Customer customer, CustomerSetTopBox customerSetTopBox, Action action, String month);

	List<CustomerLedgre> findByCustomerAndCustomerSetTopBoxAndMonth(Customer customer, CustomerSetTopBox customerSetTopBox, String month);
	
	List<CustomerLedgre> findByCustomerLedgreEntryAndMonth(CustomerLedgreEntry customerLedgreEntry, String month);

}
