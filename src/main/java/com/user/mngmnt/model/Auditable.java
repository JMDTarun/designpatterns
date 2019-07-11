package com.user.mngmnt.model;

import com.user.mngmnt.utils.AuditListener;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.time.Instant;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditListener.class)
public class Auditable implements Serializable {

    private Instant createdAt;

    private Instant updatedAt;
}
