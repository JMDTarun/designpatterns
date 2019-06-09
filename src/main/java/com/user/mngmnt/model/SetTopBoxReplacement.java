package com.user.mngmnt.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.user.mngmnt.enums.SetTopBoxReplacementStatus;
import com.user.mngmnt.enums.SetTopBoxStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "SET_TOP_BOX_REPLACEMENT")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class SetTopBoxReplacement {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	@ManyToOne
	@JoinColumn(name = "oldSetTopBoxId", referencedColumnName = "id")
	private SetTopBox oldSetTopBox;
	@ManyToOne
	@JoinColumn(name = "replacedSetTopBoxId", referencedColumnName = "id")
	private SetTopBox replacedSetTopBox;
	private SetTopBoxReplacementStatus replacementType;
	private SetTopBoxStatus replacementReason;
	private Double replacementCharge;
	@Transient
	private Long customerSetTopBoxId;
}
