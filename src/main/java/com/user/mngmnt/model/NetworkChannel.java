package com.user.mngmnt.model;

import java.time.Instant;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "NETWORK_CHANNEL")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class NetworkChannel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    private Double monthlyRent;

    private Double gst;
    
    private Double total;
    
    private Double gstPercentage;
    
    private Instant createdAt;
	
	private Instant updatedAt;
    
    @Column(unique = true)
    private String name;

    @ManyToOne()
    @JoinColumn(name = "networkId", referencedColumnName = "id")
    private Network network;

    @ManyToOne()
    @JoinColumn(name = "channelId", referencedColumnName = "id")
    private Channel channel;

}
