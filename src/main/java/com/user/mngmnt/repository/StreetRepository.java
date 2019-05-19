package com.user.mngmnt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.user.mngmnt.model.Street;

@Repository
public interface StreetRepository extends JpaRepository<Street, Long> {

    Street findByStreetNumber(String streetNumber);

    Street findByStreetNumber2(String streetNumber2);
}
