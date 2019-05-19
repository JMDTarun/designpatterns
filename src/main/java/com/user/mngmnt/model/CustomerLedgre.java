package com.user.mngmnt.model;

import java.time.Instant;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.user.mngmnt.enums.Action;
import com.user.mngmnt.enums.PaymentMode;

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

	private Date entryDate;

	private Date installationDate;

	private PaymentMode paymentMode;

	private Date paymentStartDate;

	private Date billingCycle;

	private Double discount;

	private Double openingBalance;
	
	private Instant createdAt;
	
	private Instant updatedAt;

//	@OneToMany()
//	@JoinColumn(name = "customerId", referencedColumnName = "id")
//	private Customer customer;
//
//	@OneToMany()
//	@JoinColumn(name = "areaId", referencedColumnName = "id")
//	private Area area;
//
//	@OneToMany()
//	@JoinColumn(name = "subAreaId", referencedColumnName = "id")
//	private SubArea subArea;
//
//	@OneToMany()
//	@JoinColumn(name = "streetId", referencedColumnName = "id")
//	private Street street;
//
//	@OneToMany
//	@JoinColumn(name = "setTopBoxId", referencedColumnName = "id")
//	private SetTopBox setTopBox;

}
