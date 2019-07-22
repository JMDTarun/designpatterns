package com.user.mngmnt.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerPartialPaymentColumns {
    private String customerName;
    private String customerCode;
    private String area;
    private String street;
    private String subArea;
    private String address;
    private String mobile;
    private String status;
    private Double monthlyTotal;
    private String creditOrDebit;
    private Double balance;
}
