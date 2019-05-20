package com.user.mngmnt.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.user.mngmnt.model.Customer;
import com.user.mngmnt.model.CustomerSetTopBox;
import com.user.mngmnt.model.NetworkChannel;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Customer findByName(String name);

    Customer findByMobile(String mobile);

    Customer findByLandLine(String landLine);
    
    @Query("SELECT c.customerSetTopBoxes FROM Customer c WHERE c.id = :id")
	Page<CustomerSetTopBox> getCutomerSetTopBoxes(@Param("id") long customerId, Pageable pageRequest);

    @Query("SELECT distinct s.networkChannels FROM Customer c inner join c.customerSetTopBoxes s WHERE s.id = :id")
	Page<NetworkChannel> getCutomerSetTopBoxChannels(@Param("id") long customerId, Pageable pageRequest);
    
    @Query("SELECT c FROM Pack p INNER JOIN p.networkChannels c WHERE c.id = :id")
    CustomerSetTopBox getCutomerSetTopBoxeById(@Param("id") long id);
    
}
