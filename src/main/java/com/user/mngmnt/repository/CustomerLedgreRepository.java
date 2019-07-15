package com.user.mngmnt.repository;

import java.time.Month;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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

    @Query(value = "select * from (select sum(AMOUNT) as debit, customer_id from CUSTOMER_LEDGRE  where CREDIT_DEBIT='DEBIT' and is_on_hold='false' group by customer_id) as c1, (select sum(AMOUNT) as credit, customer_id from CUSTOMER_LEDGRE  where CREDIT_DEBIT='CREDIT'  and is_on_hold='false' group by customer_id) as c2 join Customer as c on c.id=c1.customer_id where c1.customer_id=c2.customer_id and debit > credit", nativeQuery= true)
    List<CustomerLedgre> getNegativeOutstandingOfCustomers(Pageable pageable);
    
    @Query(value = "select * from (select sum(AMOUNT) as debit, customer_id from CUSTOMER_LEDGRE  where CREDIT_DEBIT='DEBIT' and is_on_hold='false' group by customer_id) as c1, (select sum(AMOUNT) as credit, customer_id from CUSTOMER_LEDGRE  where CREDIT_DEBIT='CREDIT'  and is_on_hold='false' group by customer_id) as c2 join Customer as c on c.id=c1.customer_id where c1.customer_id=c2.customer_id and debit < credit", nativeQuery= true)
    List<CustomerLedgre> getPositivrOutstandingOfCustomers(Pageable pageable);
	
    @Query(value = "select * from (select sum(AMOUNT) as debit, customer_id from CUSTOMER_LEDGRE  where CREDIT_DEBIT='DEBIT' and is_on_hold='false' group by customer_id) as c1, (select sum(AMOUNT) as credit, customer_id from CUSTOMER_LEDGRE  where CREDIT_DEBIT='CREDIT'  and is_on_hold='false' group by customer_id) as c2 join Customer as c on c.id=c1.customer_id where c1.customer_id=c2.customer_id", nativeQuery= true)
    List<CustomerLedgre> getOutstandingOfCustomers(Pageable pageable);
    
}
