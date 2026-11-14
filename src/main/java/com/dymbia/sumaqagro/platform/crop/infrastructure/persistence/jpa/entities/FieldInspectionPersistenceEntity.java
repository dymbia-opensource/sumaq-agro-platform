package com.dymbia.sumaqagro.platform.crop.infrastructure.persistence.jpa.entities;

import com.dymbia.sumaqagro.platform.crop.domain.model.valueobjects.FieldInspectionStatus;
import com.dymbia.sumaqagro.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

/**
 * JPA persistence entity for field inspections.
 */
@Entity
@Table(name = "field_inspections")
@Getter
@Setter
@NoArgsConstructor
public class FieldInspectionPersistenceEntity
        extends AuditableAbstractPersistenceEntity {

    @Column(nullable = false, unique = true)
    private Long pestReportId;

    @Column(nullable = false)
    private Long plotId;

    @Column(nullable = false)
    private Long agronomistId;

    @Column(nullable = false)
    private Instant scheduledAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FieldInspectionStatus status;

    @Column(columnDefinition = "TEXT")
    private String findings;

    @Column(nullable = true)
    private Instant completedAt;
}