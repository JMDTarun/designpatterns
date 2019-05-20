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

import com.user.mngmnt.enums.Action;
import com.user.mngmnt.enums.CreditDebit;
import com.user.mngmnt.enums.Month;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "CUSTOMER_LEDGRE")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class CustomerLedgre {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Long id;

	private Action action;

	private Instant createdAt;
	
	private Instant updatedAt;
	
	private Double amount;
	
	@Enumerated(EnumType.STRING)
	private CreditDebit creditDebit;
	
	private String reason;
	
	private Month month;

	@ManyToOne()
	@JoinColumn(name = "customerId", referencedColumnName = "id")
	private Customer customer;

	@ManyToOne()
	@JoinColumn(name = "packId", referencedColumnName = "id")
	private Pack pack;
	
	@ManyToOne
	@JoinColumn(name = "setTopBoxId", referencedColumnName = "id")
	private SetTopBox setTopBox;

}
