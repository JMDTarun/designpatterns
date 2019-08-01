package com.user.mngmnt.model;

import java.time.Instant;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.user.mngmnt.enums.SetTopBoxStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "SET_TOP_BOX_HISTORY")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class CustomerSetTopBoxHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;
    @Default
    @Enumerated(EnumType.STRING)
    private SetTopBoxStatus setTopBoxStatus = SetTopBoxStatus.DE_ACTIVATE;
    @ManyToOne
    @JoinColumn(name = "customerSetTopBoxId", referencedColumnName = "id")
    private CustomerSetTopBox customerSetTopBox;
    private Instant dateTime;
    @ManyToOne
    @JoinColumn(name = "customerId", referencedColumnName = "id")
    private Customer customer;
    @Default
    private boolean isReActivated = false;
}
