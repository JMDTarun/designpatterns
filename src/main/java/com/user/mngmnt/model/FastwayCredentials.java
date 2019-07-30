package com.user.mngmnt.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "FASTWAY_CREDENTIAL")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class FastwayCredentials extends Auditable{

    @Id
    private Long id;

    private String username;

    private String password;
}
