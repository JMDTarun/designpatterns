package com.user.mngmnt.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.user.mngmnt.enums.SetTopBoxStatus;
import com.user.mngmnt.model.SetTopBox;

@Repository
public interface SetTopBoxRepository extends JpaRepository<SetTopBox, Long> {

    SetTopBox findBySetTopBoxNumber(String number);

    SetTopBox findByCardNumber(String number);

    SetTopBox findBySafeCode(String number);

	List<SetTopBox> findBySetTopBoxStatus(SetTopBoxStatus free);

}
