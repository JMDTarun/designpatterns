package com.user.mngmnt.model;

import java.time.Instant;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "PACK")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Pack {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Long id;

	@Column(unique = true)
	private String name;
	
	private Double price;
	
	private Double gst;
    
    private Double total;
    
    private Double gstPercentage;
	
	@ManyToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "networkChannelId", referencedColumnName = "id")
	@JsonIgnore
	private Set<NetworkChannel> networkChannels;
	
    private Instant createdAt;
	
	private Instant updatedAt;

}
