package com.user.mngmnt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.user.mngmnt.model.SetTopBoxReplacement;

@Repository
public interface SetTopBoxReplacementRepository extends JpaRepository<SetTopBoxReplacement, Long> {

}
