package com.user.mngmnt.model;

import java.util.Date;

import javax.persistence.*;

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
	@Enumerated(EnumType.STRING)
	private SetTopBoxReplacementStatus replacementType;
	@Enumerated(EnumType.STRING)
	private SetTopBoxStatus replacementReason;
	private Date date;
	private Double replacementCharge;
	@ManyToOne
    @JoinColumn(name = "replacedForCustomerId", referencedColumnName = "id")
    private Customer replacedForCustomer;
	@Transient
	private Long customerSetTopBoxId;
}
