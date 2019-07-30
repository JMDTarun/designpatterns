package com.user.mngmnt.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class SetTopBoxReplacementColumns {

    private String customerName;
    private String customerCode;
    private String area;
    private String subArea;
    private String street;
    private String oldSetTopBoxNumber;
	private String replacedSetTopBoxNumber;
	private String replacementReason;
	private String replacementType;
	private Double replacementCharge;
}
