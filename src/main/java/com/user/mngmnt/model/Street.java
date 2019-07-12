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
@Table(name = "STREET")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Street extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    private String streetNumber;

    private String streetNumber2;

    @ManyToOne()
    @JoinColumn(name = "areaId", referencedColumnName = "id")
    private Area area;

}
