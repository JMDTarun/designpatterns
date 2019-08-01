package com.user.mngmnt.model;

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
@Table(name = "CUSTOMER_CODE")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class CustomerCode {
	@Id
	private Long id;

	private Long customerCode;
}
