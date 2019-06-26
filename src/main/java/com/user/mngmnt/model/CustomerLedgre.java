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

import org.springframework.format.annotation.DateTimeFormat;

import com.user.mngmnt.enums.Action;
import com.user.mngmnt.enums.CreditDebit;
import com.user.mngmnt.enums.CustomerLedgreEntry;
import com.user.mngmnt.enums.Month;
import com.user.mngmnt.enums.PaymentMode;
import com.user.mngmnt.enums.PaymentType;

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
	
	private Double amount;
	
	@DateTimeFormat(pattern = "yyyy/MM/dd")
	private Date paymentDate;
	
	@Enumerated(EnumType.STRING)
	private CreditDebit creditDebit;
	
	@Enumerated(EnumType.STRING)
	private PaymentMode paymentMode;
	
	@Enumerated(EnumType.STRING)
	private PaymentType paymentType;
	
	@DateTimeFormat(pattern = "yyyy/MM/dd")
	private Date chequeDate;
	
	private String chequeNumber;
	
	private String machineId;
	
	@Enumerated(EnumType.STRING)
	private CustomerLedgreEntry customerLedgreEntry;
	
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
