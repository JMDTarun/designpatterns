package com.user.mngmnt.model;

import com.user.mngmnt.enums.SetTopBoxReplacementStatus;
import com.user.mngmnt.enums.SetTopBoxStatus;

import lombok.Data;

@Data
public class SetTopBoxReplacement {

	private Long currentSetTopBoxId;
	private Long replacedSetTopBoxId;
	private SetTopBoxReplacementStatus replacementType;
	private SetTopBoxStatus replacementReason;
	private Double replacementCharge;
	
}
