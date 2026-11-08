package com.dymbia.sumaqagro.platform.crop.interfaces.rest.resources;

import com.dymbia.sumaqagro.platform.crop.domain.model.valueobjects.PestReportSeverity;

/**
 * API request for recording a pest report.
 */
public record RecordPestReportResource(
        Long plotId,
        Long reporterId,
        String description,
        PestReportSeverity severity
) {

    public RecordPestReportResource {
        if (plotId == null || plotId <= 0) {
            throw new IllegalArgumentException("Plot ID must be greater than zero");
        }

        if (reporterId == null || reporterId <= 0) {
            throw new IllegalArgumentException("Reporter ID must be greater than zero");
        }

        if (description == null || description.trim().length() < 10) {
            throw new IllegalArgumentException("Description must contain at least 10 characters");
        }

        if (severity == null) {
            throw new IllegalArgumentException("Severity is required");
        }

        description = description.trim();
    }
}