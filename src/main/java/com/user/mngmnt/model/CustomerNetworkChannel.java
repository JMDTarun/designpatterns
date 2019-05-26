package com.user.mngmnt.model;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
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
	
	@DateTimeFormat(pattern = "yyyy/MM/dd")
	private Date paymentStartDate;
	
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "setTopBoxId", referencedColumnName = "id")
	private NetworkChannel networkChannel;
	
}
