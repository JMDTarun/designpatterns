package com.user.mngmnt.model;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class PaymentReceiptColumns {

    private String customerName;
    private Long customerCode;
    private String area;
    private String subArea;
    private String street;
	private Double amount;
	private String chequeNumber;
	private Date chequeDate;
	private Date paymentDate;
	private String paymentMode;
	private String paymentType;
	
}
