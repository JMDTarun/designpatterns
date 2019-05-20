package com.user.mngmnt.model;

import java.time.Instant;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.user.mngmnt.enums.SetTopBoxStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "SET_TOP_BOX")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class SetTopBox {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Long id;
	@Column(unique = true)
	private String setTopBoxNumber;
	@Column(unique = true)
	private String cardNumber;
	private String safeCode;
    private Instant createdAt;
	private Instant updatedAt;
	@Default
	@Enumerated(EnumType.STRING)
	private SetTopBoxStatus setTopBoxStatus = SetTopBoxStatus.FREE;
	private String reason;
}
