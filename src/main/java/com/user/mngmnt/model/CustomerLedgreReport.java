package com.user.mngmnt.model;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerLedgreReport {
	private Date date;
	private String action;
    private String customerLedgreEntry;
	private Double credit;
	private Double debit;
	private String creditOrDebit;
	private Double balance;
}
