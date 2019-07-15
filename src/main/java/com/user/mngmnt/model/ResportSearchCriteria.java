package com.user.mngmnt.model;

import java.util.Date;

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
	private Date startDate;
	private Date endDate;
	private Integer paymentDayStart;
	private Integer paymentDayEnd;
	private Double rangeStart;
	private Double rangeEnd;
	private Integer countSetTopBoxes;
	private Integer assignedSetTopBoxes;
	private String dateBetween;
	private String numberBewtween;
}
