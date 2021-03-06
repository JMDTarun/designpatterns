package com.user.mngmnt.model;

import java.time.Instant;
import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.user.mngmnt.enums.CustomerSetTopBoxStatus;
import com.user.mngmnt.enums.DiscountFrequency;
import com.user.mngmnt.enums.PaymentMode;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "CUSTOMER_SET_TOP_BOX")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class CustomerSetTopBox extends Auditable{

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Long id;

	private PaymentMode paymentMode;

	@DateTimeFormat(pattern = "yyyy/MM/dd")
	private Date entryDate;

	@DateTimeFormat(pattern = "yyyy/MM/dd")
	private Date paymentStartDate;

	@DateTimeFormat(pattern = "yyyy/MM/dd")
	private Date billingCycle;
	
	private Integer billingDay;

	@Default
	private Double openingBalance = 0.0;

	@Default
	private Double discount = 0.0;

	private DiscountFrequency discountFrequency;

	@DateTimeFormat(pattern = "yyyy/MM/dd")
	private Date deactivateDate;

	@DateTimeFormat(pattern = "yyyy/MM/dd")
	private Date activateDate;

	private String activateReason;

	private String deactivateReason;

	@Default
	private boolean isDeleted = false;

	@Default
	private boolean isActive = true;

	@Default
	@Enumerated(EnumType.STRING)
	private CustomerSetTopBoxStatus customerSetTopBoxStatus = CustomerSetTopBoxStatus.ACTIVE;

	@ManyToOne
	@JoinColumn(name = "packId", referencedColumnName = "id")
	private Pack pack;

	private Double packPrice;
	
	@Default
    private Double monthlyTotal = 0.0;

	@Default
	private Double packPriceDifference = 0.0;
	
	@Default
	private Double setTopBoxPrice = 0.0;
	
	private Integer paymentDay;
	
	@ManyToOne
	@JoinColumn(name = "setTopBoxId", referencedColumnName = "id")
	private SetTopBox setTopBox;

	@ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
	@JoinColumn(name = "customerNetworkChannelId", referencedColumnName = "id")
	@JsonIgnore
	private Set<CustomerNetworkChannel> customerNetworkChannels;
	
	@ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
	@JoinColumn(name = "customerSetTopBoxReplacementId", referencedColumnName = "id")
	@JsonIgnore
	private Set<SetTopBoxReplacement> customerSetTopBoxReplacements;
	
}
