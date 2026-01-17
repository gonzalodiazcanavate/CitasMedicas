package com.gdc.medicalapp.domain.entities;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;

import java.time.OffsetDateTime;

@MappedSuperclass
public abstract class BaseEntity {
    @Column(name = "created_at", nullable = false, updatable = false)
    protected OffsetDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = OffsetDateTime.now();
    }
}
