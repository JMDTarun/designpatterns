package com.user.mngmnt.model;

import java.time.Instant;
import java.util.Date;

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
import lombok.Builder.Default;
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

	@Enumerated(EnumType.STRING)
	private Action action;

	private Instant createdAt;
	
	private Instant updatedAt;
	
	private Double amount;
	
	@Enumerated(EnumType.STRING)
	private CreditDebit creditDebit;
	
	private String reason;
	
	private Month month;
	
	private Date activateDate;
	
	private Date deactivateDate;

	@Default
	private boolean isOnHold = false;
	
	@ManyToOne()
	@JoinColumn(name = "customerId", referencedColumnName = "id")
	private Customer customer;

	@ManyToOne()
	@JoinColumn(name = "customerNetworkChannelId", referencedColumnName = "id")
	private CustomerNetworkChannel customerNetworkChannel;
	
	@ManyToOne()
	@JoinColumn(name = "customerSetTopBoxId", referencedColumnName = "id")
	private CustomerSetTopBox customerSetTopBox;

}
