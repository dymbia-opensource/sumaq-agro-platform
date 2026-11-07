package com.dymbia.sumaqagro.platform.crop.domain.model.aggregates;

import com.dymbia.sumaqagro.platform.crop.domain.model.valueobjects.AgroclimaticAlertSeverity;
import com.dymbia.sumaqagro.platform.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.Instant;
import java.util.Objects;

/**
 * Agroclimatic alert issued for a specific region.
 */

@Getter
@Entity
@Table(name = "agroclimatic_alerts")
public class AgroclimaticAlert
        extends AuditableAbstractAggregateRoot<AgroclimaticAlert> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AgroclimaticAlertSeverity severity;

    @Column(nullable = false)
    private String region;

    @Column(nullable = false)
    private Instant issuedAt;

    @Column(nullable = false)
    private String source;

    protected AgroclimaticAlert() {
        // Required by JPA.
    }

    public AgroclimaticAlert(
            String title,
            String description,
            AgroclimaticAlertSeverity severity,
            String region,
            Instant issuedAt,
            String source
    ) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException(
                    "Alert title is required"
            );
        }

        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException(
                    "Alert description is required"
            );
        }

        if (region == null || region.isBlank()) {
            throw new IllegalArgumentException(
                    "Alert region is required"
            );
        }

        if (source == null || source.isBlank()) {
            throw new IllegalArgumentException(
                    "Alert source is required"
            );
        }

        this.title = title.trim();
        this.description = description.trim();
        this.severity = Objects.requireNonNull(
                severity, "Alert severity is required"
        );
        this.region = region.trim();
        this.issuedAt = Objects.requireNonNull(
                issuedAt, "Issue time is required"
        );
        this.source = source.trim();
    }
}