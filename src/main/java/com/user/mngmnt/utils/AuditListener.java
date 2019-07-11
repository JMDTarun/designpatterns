package com.user.mngmnt.utils;

import com.user.mngmnt.model.Auditable;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.Instant;

public class AuditListener {

    @PrePersist
    void onPrePersist(Object entity) {
        if(entity instanceof Auditable){
            Instant now = Instant.now();
            Auditable auditable = (Auditable) entity;
            auditable.setCreatedAt(now);
            auditable.setUpdatedAt(now);
        }
    }

    @PreUpdate
    void onPreUpdate(Object entity) {
        if(entity instanceof Auditable){
            Instant now = Instant.now();
            Auditable auditable = (Auditable) entity;
            auditable.setUpdatedAt(now);
        }
    }
}
