package com.user.mngmnt.model;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReportSearchCriteria {
	private Integer areaId;
	private Integer subAreaId;
	private Integer streetId;
	private String customerStatus;
	private Double monthlyCharge;
	private Integer packId;
	private Double packPrice;
	private Integer outstandingValue;
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
	private Integer prAreaId;
	private Integer prSubAreaId;
	private Integer prStreetId;
	private String prMachineNumner;
	private Date prFromDate;
	private Date prToDate;
	@DateTimeFormat(pattern = "yyyy/MM/dd")
	private String prPaymentMode;
	@DateTimeFormat(pattern = "yyyy/MM/dd")
	private String prPaymentType;
	private boolean isPaymentReceiptReport = false;
	private Integer customerType;
	private String replacementReason;
	private String replacementType;
	private String replacementAmountStart;
	private String replacementAmountEnd;
	private String setTopBoxStatus;
}
