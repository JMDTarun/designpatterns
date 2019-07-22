package com.user.mngmnt.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "PLAN_CHANGE_CONTROL")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class PlanChangeControl extends Auditable{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String serialNumber;

    @Enumerated(EnumType.STRING)
    private PlanChangeControlStatus status;

    private String errMsg;

    private Long executionId;

    @Enumerated(EnumType.STRING)
    private PlanChangeControlAction action;

    private String plans; //Comma separated plans(Ala catre, Broadcaster plans, packs)
}
