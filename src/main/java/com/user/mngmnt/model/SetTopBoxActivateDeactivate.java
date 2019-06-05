package com.user.mngmnt.model;

import java.util.Date;

import lombok.Data;

@Data
public class SetTopBoxActivateDeactivate {
	private Long id;
	private Long customerId;
	private Long customerSetTopBoxId;
	private String reason;
	private Date date;
}
