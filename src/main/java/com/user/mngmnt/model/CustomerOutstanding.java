package com.user.mngmnt.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class CustomerOutstanding {
    private Double credit;
    private Double debit;
    private Customer customer;
}
