package com.user.mngmnt.model;

import com.user.mngmnt.enums.SetTopBoxStatus;

import lombok.Data;

@Data
public class RemoveCustomerSetTopBox {
	private Long id;
	private String reason;
	private SetTopBoxStatus setTopBoxStatus;
	private Double amount;
}
