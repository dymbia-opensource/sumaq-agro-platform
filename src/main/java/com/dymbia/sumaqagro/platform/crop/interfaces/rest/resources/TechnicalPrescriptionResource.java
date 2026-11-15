package com.dymbia.sumaqagro.platform.crop.interfaces.rest.resources;

import com.dymbia.sumaqagro.platform.crop.domain.model.valueobjects.TechnicalPrescriptionStatus;

import java.time.Instant;
import java.time.LocalDate;

/**
 * API response containing technical prescription information.
 */
public record TechnicalPrescriptionResource(
        Long id,
        Long pestReportId,
        Long plotId,
        Long agronomistId,
        String product,
        String dosage,
        String instructions,
        LocalDate applicationDate,
        TechnicalPrescriptionStatus status,
        Instant appliedAt
) {
}
