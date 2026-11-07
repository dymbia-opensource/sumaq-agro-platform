package com.dymbia.sumaqagro.platform.crop.domain.model.aggregates;

import com.dymbia.sumaqagro.platform.crop.domain.model.valueobjects.NdviIndex;
import com.dymbia.sumaqagro.platform.crop.domain.model.valueobjects.NdwiIndex;
import com.dymbia.sumaqagro.platform.crop.domain.model.valueobjects.Percentage;
import com.dymbia.sumaqagro.platform.crop.infrastructure.persistence.jpa.converters.NdviIndexAttributeConverter;
import com.dymbia.sumaqagro.platform.crop.infrastructure.persistence.jpa.converters.NdwiIndexAttributeConverter;
import com.dymbia.sumaqagro.platform.crop.infrastructure.persistence.jpa.converters.PercentageAttributeConverter;
import com.dymbia.sumaqagro.platform.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.Instant;
import java.util.Objects;

/**
 * Satellite observation associated with a plot and capture time.
 */

@Getter
@Entity
@Table(name = "satellite_observations")
public class SatelliteObservation extends AuditableAbstractAggregateRoot<SatelliteObservation> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long plotId;

    @Column(nullable = false)
    private Instant capturedAt;

    @Column(nullable = false)
    private String source;

    @Convert(converter = NdviIndexAttributeConverter.class)
    private NdviIndex ndviMean;

    @Convert(converter = NdwiIndexAttributeConverter.class)
    private NdwiIndex ndwiMean;

    @Convert(converter = PercentageAttributeConverter.class)
    private Percentage cloudCoveragePercent;

    private Double surfaceTempKelvin;

    protected SatelliteObservation() {
        // Required by JPA.
    }

    public SatelliteObservation(
            Long plotId,
            Instant capturedAt,
            String source,
            NdviIndex ndviMean,
            NdwiIndex ndwiMean,
            Percentage cloudCoveragePercent,
            Double surfaceTempKelvin
    ) {
        if (plotId == null || plotId <= 0) {
            throw new IllegalArgumentException(
                    "Plot ID must be greater than zero"
            );
        }

        Objects.requireNonNull(capturedAt, "Capture time is required");

        if (source == null || source.isBlank()) {
            throw new IllegalArgumentException(
                    "Observation source is required"
            );
        }

        if (ndviMean == null && ndwiMean == null) {
            throw new IllegalArgumentException(
                    "At least one vegetation or water index is required"
            );
        }

        if (surfaceTempKelvin != null
                && (!Double.isFinite(surfaceTempKelvin)
                || surfaceTempKelvin < 0.0)) {
            throw new IllegalArgumentException(
                    "Surface temperature must be finite and non-negative"
            );
        }

        this.plotId = plotId;
        this.capturedAt = capturedAt;
        this.source = source.trim();
        this.ndviMean = ndviMean;
        this.ndwiMean = ndwiMean;
        this.cloudCoveragePercent = cloudCoveragePercent;
        this.surfaceTempKelvin = surfaceTempKelvin;
    }
}