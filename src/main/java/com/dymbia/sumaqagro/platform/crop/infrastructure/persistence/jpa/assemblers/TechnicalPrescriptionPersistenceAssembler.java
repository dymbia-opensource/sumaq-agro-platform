package com.dymbia.sumaqagro.platform.crop.infrastructure.persistence.jpa.assemblers;

import com.dymbia.sumaqagro.platform.crop.domain.model.aggregates.TechnicalPrescription;
import com.dymbia.sumaqagro.platform.crop.infrastructure.persistence.jpa.entities.TechnicalPrescriptionPersistenceEntity;

/**
 * Converts between technical prescription domain and persistence representations.
 */
public final class TechnicalPrescriptionPersistenceAssembler {

    private TechnicalPrescriptionPersistenceAssembler() {
    }

    /**
     * Reconstructs a domain aggregate from a persisted entity.
     */
    public static TechnicalPrescription toDomainFromPersistence(TechnicalPrescriptionPersistenceEntity entity) {
        if (entity == null) {
            return null;
        }

        return TechnicalPrescription.reconstitute(
                entity.getId(),
                entity.getPestReportId(),
                entity.getPlotId(),
                entity.getAgronomistId(),
                entity.getProduct(),
                entity.getDosage(),
                entity.getInstructions(),
                entity.getApplicationDate(),
                entity.getStatus(),
                entity.getAppliedAt()
        );
    }

    /**
     * Converts a domain aggregate into a JPA entity.
     */
    public static TechnicalPrescriptionPersistenceEntity toPersistenceFromDomain(TechnicalPrescription prescription) {
        if (prescription == null) {
            return null;
        }

        var entity = new TechnicalPrescriptionPersistenceEntity();

        if (prescription.getId() != null) {
            entity.setId(prescription.getId());
        }

        entity.setPestReportId(prescription.getPestReportId());
        entity.setPlotId(prescription.getPlotId());
        entity.setAgronomistId(prescription.getAgronomistId());
        entity.setProduct(prescription.getProduct());
        entity.setDosage(prescription.getDosage());
        entity.setInstructions(prescription.getInstructions());
        entity.setApplicationDate(prescription.getApplicationDate());
        entity.setStatus(prescription.getStatus());
        entity.setAppliedAt(prescription.getAppliedAt());

        return entity;
    }
}