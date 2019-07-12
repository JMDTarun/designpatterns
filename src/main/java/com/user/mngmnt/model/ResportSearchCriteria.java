package com.user.mngmnt.model;

import lombok.AllArgsConstructor;
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
	private Integer countSetTopBoxes;
	private Integer assignedSetTopBoxes;
	private String dateBetween;
	private String numberBewtween;
}
