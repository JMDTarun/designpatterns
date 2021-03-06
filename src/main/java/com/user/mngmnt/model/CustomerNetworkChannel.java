package com.user.mngmnt.model;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "CUSTOMER_NETWORK_CHANNEL")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class CustomerNetworkChannel {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Long id;
	
	@Default
	private boolean isDeleted = false;
	
	private String reason;
	
	@DateTimeFormat(pattern = "yyyy/MM/dd")
	private Date paymentStartDate;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "networkChannelId", referencedColumnName = "id")
	private NetworkChannel networkChannel;
	
}
