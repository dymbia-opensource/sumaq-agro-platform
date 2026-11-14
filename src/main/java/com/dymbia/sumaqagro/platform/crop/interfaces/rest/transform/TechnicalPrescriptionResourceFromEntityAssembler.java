package com.dymbia.sumaqagro.platform.crop.interfaces.rest.transform;

import com.dymbia.sumaqagro.platform.crop.domain.model.aggregates.TechnicalPrescription;
import com.dymbia.sumaqagro.platform.crop.interfaces.rest.resources.TechnicalPrescriptionResource;

/**
 * Converts a technical prescription aggregate into an API response.
 */
public class TechnicalPrescriptionResourceFromEntityAssembler {

    /**
     * Converts a {@link TechnicalPrescription} entity into a {@link TechnicalPrescriptionResource}.
     *
     * @param prescription The technical prescription entity to convert.
     * @return The resulting resource.
     */
    public static TechnicalPrescriptionResource toResourceFromEntity(TechnicalPrescription prescription) {
        return new TechnicalPrescriptionResource(
                prescription.getId(),
                prescription.getPestReportId(),
                prescription.getPlotId(),
                prescription.getAgronomistId(),
                prescription.getProduct(),
                prescription.getDosage(),
                prescription.getInstructions(),
                prescription.getApplicationDate(),
                prescription.getStatus(),
                prescription.getAppliedAt()
        );
    }
}
