package com.user.mngmnt.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.user.mngmnt.model.CustomerLedgre;
import com.user.mngmnt.model.ReportSearchCriteria;

public interface ReportsRepository {
    
    List<CustomerLedgre> findAllWithPartialPayment(ReportSearchCriteria resportSearchCriteria, Pageable pageable);
}
