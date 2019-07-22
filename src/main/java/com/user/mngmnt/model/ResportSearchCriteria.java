package com.user.mngmnt.model;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.AllArgsConstructor;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResportSearchCriteria {
	private Integer areaId;
	private Integer subAreaId;
	private Integer streetId;
	private String customerStatus;
	private Double monthlyCharge;
	private Integer packId;
	private Double packPrice;
	private Boolean isGreaterThenZero;
	@DateTimeFormat(pattern = "MM/dd/yyyy")
	private Date startDate;
	@DateTimeFormat(pattern = "MM/dd/yyyy")
	private Date endDate;
	private Integer paymentDayStart;
	private Integer paymentDayEnd;
	private Double rangeStart;
	private Double rangeEnd;
	private Integer countSetTopBoxes;
	private Integer assignedSetTopBoxes;
	private String dateBetween;
	private String numberBetween;
	private String customerId;
	private String month;
	@DateTimeFormat(pattern = "yyyy/MM/dd")
	private Date start;
	@DateTimeFormat(pattern = "yyyy/MM/dd")
	private Date end;
	private boolean isNoPaymentBetween;
	
}
