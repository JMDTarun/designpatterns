package com.user.mngmnt.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.user.mngmnt.model.CustomerSetTopBox;
import com.user.mngmnt.model.SetTopBox;

@Repository
public interface CustomerSetTopBoxRepository extends JpaRepository<CustomerSetTopBox, Long> {

    @Query("select c from CustomerSetTopBox c where c.setTopBox.setTopBoxNumber = :number")
    List<CustomerSetTopBox> findBySetTopBoxNumber(String number);
    
    @Query("Select DISTINCT(c.packPrice) from CustomerSetTopBox c")
    List<Double> findDistinctPackPrices();

}
