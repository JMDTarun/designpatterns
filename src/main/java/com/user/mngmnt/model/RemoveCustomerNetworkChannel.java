package com.user.mngmnt.model;

import java.util.Date;

import lombok.Data;

@Data
public class RemoveCustomerNetworkChannel {
	private Long id;
	private Long customerId;
	private Long customerSetTopBoxId;
	private Date paymentStartDate;
	private String reason;
}
