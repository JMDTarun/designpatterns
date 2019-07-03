package com.user.mngmnt.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerReport {
	private Customer customer;
	private String status;
	private Double monthlyTotal;
	private Double channelTotal;
	private Integer totalSetTopBoxes;
	private Integer totalChannels;
}
