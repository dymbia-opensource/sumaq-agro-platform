package com.dymbia.sumaqagro.platform.crop.infrastructure.persistence.jpa.assemblers;

import com.dymbia.sumaqagro.platform.crop.domain.model.aggregates.FieldInspection;
import com.dymbia.sumaqagro.platform.crop.infrastructure.persistence.jpa.entities.FieldInspectionPersistenceEntity;

/**
 * Converts between field inspection domain and persistence representations.
 */
public final class FieldInspectionPersistenceAssembler {

    private FieldInspectionPersistenceAssembler() {
    }

    /**
     * Reconstructs a domain aggregate from a persisted entity.
     */
    public static FieldInspection toDomainFromPersistence(FieldInspectionPersistenceEntity entity) {
        if (entity == null) return null;

        return FieldInspection.reconstitute(
                entity.getId(),
                entity.getPestReportId(),
                entity.getPlotId(),
                entity.getAgronomistId(),
                entity.getScheduledAt(),
                entity.getStatus(),
                entity.getFindings(),
                entity.getCompletedAt()
        );
    }

    /**
     * Converts a domain aggregate into a JPA entity.
     */
    public static FieldInspectionPersistenceEntity toPersistenceFromDomain(FieldInspection inspection) {
        if (inspection == null) return null;

        var entity = new FieldInspectionPersistenceEntity();

        if (inspection.getId() != null) {
            entity.setId(inspection.getId());
        }

        entity.setPestReportId(inspection.getPestReportId());
        entity.setPlotId(inspection.getPlotId());
        entity.setAgronomistId(inspection.getAgronomistId());
        entity.setScheduledAt(inspection.getScheduledAt());
        entity.setStatus(inspection.getStatus());
        entity.setFindings(inspection.getFindings());
        entity.setCompletedAt(inspection.getCompletedAt());

        return entity;
    }
}