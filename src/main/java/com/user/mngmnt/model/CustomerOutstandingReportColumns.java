package com.user.mngmnt.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerOutstandingReportColumns {
    private String customerName;
    private String customerCode;
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
    private String pack;
    private List<String> networkChannels;
}