package com.dymbia.sumaqagro.platform.crop.infrastructure.persistence.jpa.entities;

import com.dymbia.sumaqagro.platform.crop.domain.model.valueobjects.NdviIndex;
import com.dymbia.sumaqagro.platform.crop.domain.model.valueobjects.NdwiIndex;
import com.dymbia.sumaqagro.platform.crop.domain.model.valueobjects.Percentage;
import com.dymbia.sumaqagro.platform.crop.infrastructure.persistence.jpa.converters.NdviIndexPersistenceConverter;
import com.dymbia.sumaqagro.platform.crop.infrastructure.persistence.jpa.converters.NdwiIndexPersistenceConverter;
import com.dymbia.sumaqagro.platform.crop.infrastructure.persistence.jpa.converters.PercentagePersistenceConverter;
import com.dymbia.sumaqagro.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

/**
 * JPA persistence entity for satellite observations.
 */
@Entity
@Table(name = "satellite_observations", uniqueConstraints =
        @UniqueConstraint(name = "uk_satellite_observations_import_key", columnNames = "import_key"))
@Getter
@Setter
@NoArgsConstructor
public class SatelliteObservationPersistenceEntity
        extends AuditableAbstractPersistenceEntity {

    @Column(nullable = false)
    private Long plotId;

    @Column(nullable = false)
    private Instant capturedAt;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String source;

    @Convert(converter = NdviIndexPersistenceConverter.class)
    @Column(nullable = true)
    private NdviIndex ndviMean;

    @Convert(converter = NdwiIndexPersistenceConverter.class)
    @Column(nullable = true)
    private NdwiIndex ndwiMean;

    @Convert(converter = PercentagePersistenceConverter.class)
    @Column(nullable = false)
    private Percentage cloudCoveragePercent;

    @Column(nullable = true)
    private Double surfaceTempKelvin;

    @Column(name = "import_key", length = 32)
    private String importKey;
}
