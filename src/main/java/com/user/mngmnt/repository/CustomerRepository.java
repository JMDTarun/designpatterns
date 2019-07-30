package com.user.mngmnt.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.user.mngmnt.model.Customer;
import com.user.mngmnt.model.CustomerNetworkChannel;
import com.user.mngmnt.model.CustomerSetTopBox;
import com.user.mngmnt.model.SetTopBoxReplacement;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Customer findByName(String name);

    Customer findByMobile(String mobile);

    Customer findByLandLine(String landLine);
    
    @Query(value = "SELECT s FROM Customer c inner join c.customerSetTopBoxes s WHERE s.isDeleted = 'false' and c.id = :id")
	Page<CustomerSetTopBox> getCutomerSetTopBoxes(@Param("id") long customerId, Pageable pageRequest);

    //@Query("SELECT distinct s.customerNetworkChannels FROM Customer c inner join c.customerSetTopBoxes s WHERE s.id = :id")
    @Query(value = "SELECT distinct nc FROM Customer c inner join c.customerSetTopBoxes s inner join s.customerNetworkChannels nc  WHERE nc.isDeleted = 'false' and s.id = :setTopBoxId")
    Page<CustomerNetworkChannel> getCutomerSetTopBoxChannels(@Param("setTopBoxId") long customerId, Pageable pageRequest);
    
    @Query(value = "SELECT c FROM Pack p INNER JOIN p.networkChannels c WHERE c.id = :id")
    CustomerSetTopBox getCutomerSetTopBoxeById(@Param("id") long id);
  
    @Query(value = "SELECT count(c) FROM Customer c WHERE c.isDeleted = 'false'")
    Integer getCustomerCount();

    @Query(value = "SELECT c FROM Customer c WHERE c.deleted = 'false' and c.customerCode = :customerCode")
    Customer findByCustomerCode(@Param("customerCode") String customerCode);

    @Query(value = "SELECT distinct rp FROM Customer c inner join c.customerSetTopBoxes s inner join s.customerSetTopBoxReplacements rp ")
    List<SetTopBoxReplacement> getCustomerSetTopBoxReplacements(Pageable pageRequest);

    @Query(value = "SELECT distinct rp FROM Customer c inner join c.customerSetTopBoxes s inner join s.customerSetTopBoxReplacements rp  WHERE c.customerCode = :customerCode")
    List<SetTopBoxReplacement> getCustomerSetTopBoxReplacementsForCustomer(@Param("customerCode") long customerId, Pageable pageRequest);
}
