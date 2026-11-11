package com.dymbia.sumaqagro.platform.crop.interfaces.rest.resources;

import java.time.LocalDate;

/**
 * API request for issuing a technical prescription.
 */
public record IssueTechnicalPrescriptionResource(
        Long pestReportId,
        Long agronomistId,
        String product,
        String dosage,
        String instructions,
        LocalDate applicationDate
) {
}
