package com.dymbia.sumaqagro.platform.crop.infrastructure.persistence.jpa.assemblers;

import com.dymbia.sumaqagro.platform.crop.domain.model.aggregates.PestReport;
import com.dymbia.sumaqagro.platform.crop.infrastructure.persistence.jpa.entities.PestReportPersistenceEntity;

/**
 * Converts between pest report domain and persistence representations.
 */
public final class PestReportPersistenceAssembler {

    private PestReportPersistenceAssembler() {
    }

    /**
     * Reconstructs a domain aggregate from a persisted entity.
     */
    public static PestReport toDomainFromPersistence(PestReportPersistenceEntity entity) {
        if (entity == null) return null;

        return PestReport.reconstitute(
                entity.getId(),
                entity.getPlotId(),
                entity.getReporterId(),
                entity.getDescription(),
                entity.getSeverity(),
                entity.getStatus()
        );
    }

    /**
     * Converts a domain aggregate into a JPA entity.
     */
    public static PestReportPersistenceEntity toPersistenceFromDomain(PestReport pestReport) {
        if (pestReport == null) return null;

        var entity = new PestReportPersistenceEntity();

        if (pestReport.getId() != null) {
            entity.setId(pestReport.getId());
        }

        entity.setPlotId(pestReport.getPlotId());
        entity.setReporterId(pestReport.getReporterId());
        entity.setDescription(pestReport.getDescription());
        entity.setSeverity(pestReport.getSeverity());
        entity.setStatus(pestReport.getStatus());

        return entity;
    }
}