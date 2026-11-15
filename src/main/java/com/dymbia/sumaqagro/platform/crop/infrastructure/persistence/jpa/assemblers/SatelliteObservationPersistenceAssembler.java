package com.dymbia.sumaqagro.platform.crop.infrastructure.persistence.jpa.assemblers;

import com.dymbia.sumaqagro.platform.crop.domain.model.aggregates.SatelliteObservation;
import com.dymbia.sumaqagro.platform.crop.infrastructure.persistence.jpa.entities.SatelliteObservationPersistenceEntity;

/**
 * Converts between satellite observation domain and persistence representations.
 */
public final class SatelliteObservationPersistenceAssembler {

    private SatelliteObservationPersistenceAssembler() {
    }

    /**
     * Reconstructs a domain aggregate from a persisted entity.
     */
    public static SatelliteObservation toDomainFromPersistence(SatelliteObservationPersistenceEntity entity) {
        if (entity == null) return null;

        return SatelliteObservation.reconstitute(
                entity.getId(),
                entity.getPlotId(),
                entity.getCapturedAt(),
                entity.getSource(),
                entity.getNdviMean(),
                entity.getNdwiMean(),
                entity.getCloudCoveragePercent(),
                entity.getSurfaceTempKelvin()
        );
    }

    /**
     * Converts a domain aggregate to its persistence representation.
     */
    public static SatelliteObservationPersistenceEntity toPersistenceFromDomain(SatelliteObservation observation) {
        if (observation == null) return null;

        var entity = new SatelliteObservationPersistenceEntity();

        if (observation.getId() != null) {
            entity.setId(observation.getId());
        }

        entity.setPlotId(observation.getPlotId());
        entity.setCapturedAt(observation.getCapturedAt());
        entity.setSource(observation.getSource());
        entity.setNdviMean(observation.getNdviMean());
        entity.setNdwiMean(observation.getNdwiMean());
        entity.setCloudCoveragePercent(observation.getCloudCoveragePercent());
        entity.setSurfaceTempKelvin(observation.getSurfaceTempKelvin());

        return entity;
    }
}
