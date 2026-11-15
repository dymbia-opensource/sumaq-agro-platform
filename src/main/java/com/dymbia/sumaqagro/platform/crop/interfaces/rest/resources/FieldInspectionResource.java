package com.dymbia.sumaqagro.platform.crop.interfaces.rest.resources;

import com.dymbia.sumaqagro.platform.crop.domain.model.valueobjects.FieldInspectionStatus;

import java.time.Instant;

/**
 * API response containing field inspection information.
 */
public record FieldInspectionResource(
        Long id,
        Long pestReportId,
        Long plotId,
        Long agronomistId,
        Instant scheduledAt,
        FieldInspectionStatus status,
        String findings,
        Instant completedAt
) {
}