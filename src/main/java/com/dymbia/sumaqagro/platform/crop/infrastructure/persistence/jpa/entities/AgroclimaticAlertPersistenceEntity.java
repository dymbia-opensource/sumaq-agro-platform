package com.dymbia.sumaqagro.platform.crop.infrastructure.persistence.jpa.entities;

import com.dymbia.sumaqagro.platform.crop.domain.model.valueobjects.AgroclimaticAlertSeverity;
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
 * JPA persistence entity for agroclimatic alerts.
 */
@Entity
@Table(name = "agroclimatic_alerts")
@Getter
@Setter
@NoArgsConstructor
public class AgroclimaticAlertPersistenceEntity extends AuditableAbstractPersistenceEntity {

    @Column(nullable = false, columnDefinition = "TEXT")
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AgroclimaticAlertSeverity severity;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String region;

    @Column(nullable = false)
    private Instant issuedAt; // Timestamp of when the alert was issued

    @Column(nullable = false, columnDefinition = "TEXT")
    private String source; // Source of the alert (e.g., meteorological service, internal system)
}
