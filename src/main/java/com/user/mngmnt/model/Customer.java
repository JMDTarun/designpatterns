package com.user.mngmnt.model;

import java.time.Instant;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "CUSTOMER")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Customer {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Long id;

	private String name;
	
	private String customerCode;

	private String address;

	private String city;

	private String mobile;

	private String landLine;
	
	private Instant createdAt;
	
	private Instant updatedAt;
	
	@Default
	private boolean isDeleted = false;
	
	@ManyToOne()
	@JoinColumn(name = "customerTypeId", referencedColumnName = "id")
	private CustomerType customerType;

	@ManyToOne()
	@JoinColumn(name = "areaId", referencedColumnName = "id")
	private Area area;

	@ManyToOne()
	@JoinColumn(name = "subAreaId", referencedColumnName = "id")
	private SubArea subArea;

	@ManyToOne()
	@JoinColumn(name = "streetId", referencedColumnName = "id")
	private Street street;

	@ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "setTopBoxId", referencedColumnName = "id")
	@JsonIgnore
	private List<CustomerSetTopBox> customerSetTopBoxes;

}
