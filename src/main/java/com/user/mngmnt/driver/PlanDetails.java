package com.user.mngmnt.driver;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PlanDetails {

    private String listName;

    private List<String> plans;

    private String reason;
}
