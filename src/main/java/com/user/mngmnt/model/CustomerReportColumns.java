package com.user.mngmnt.model;

import java.util.Date;
import java.util.List;

import com.user.mngmnt.model.CustomerReport.CustomerReportBuilder;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerReportColumns {
	private String customerName;
	private Long customerCode;
	private String area;
	private String street;
	private String subArea;
	private String address;
	private String mobile;
	private String status;
	private Double monthlyTotal;
	private Double channelTotal;
	private Integer totalSetTopBoxes;
	private Integer totalChannels;
	private Double outstanding;
	private Date entryDate;
	private String setTopBoxNumber;
	private String setTopBoxes;
	private List<String> networkChannels;
}
