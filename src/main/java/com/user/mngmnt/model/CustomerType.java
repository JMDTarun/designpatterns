package com.user.mngmnt.model;

import java.time.Instant;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "CUSTOMER_TYPE")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class CustomerType {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Long id;
	private String customerType;
	private Double maxAmount;
	private Instant createdAt;
	private Instant updatedAt;
}
