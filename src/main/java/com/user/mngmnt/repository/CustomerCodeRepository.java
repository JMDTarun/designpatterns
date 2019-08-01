package com.user.mngmnt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.user.mngmnt.model.CustomerCode;

@Repository
public interface CustomerCodeRepository extends JpaRepository<CustomerCode, Long>{
   
}
