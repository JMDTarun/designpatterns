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
public class SetTopBoxActiveDeactiveColumns {

    private String customerName;
    private String customerCode;
    private String area;
    private String subArea;
    private String street;
    private Date dateTime;
    private String setTopBoxStatus;
}
