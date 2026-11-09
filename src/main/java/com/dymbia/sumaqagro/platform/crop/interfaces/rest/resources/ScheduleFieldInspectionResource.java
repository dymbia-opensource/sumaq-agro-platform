package com.dymbia.sumaqagro.platform.crop.interfaces.rest.resources;

import java.time.Instant;

/**
 * API request for scheduling a field inspection.
 */
public record ScheduleFieldInspectionResource(Long pestReportId, Long agronomistId, Instant scheduledAt) {

    public ScheduleFieldInspectionResource {
        if (pestReportId == null || pestReportId <= 0) {
            throw new IllegalArgumentException(
                    "Pest report ID must be greater than zero"
            );
        }

        if (agronomistId == null || agronomistId <= 0) {
            throw new IllegalArgumentException(
                    "Agronomist ID must be greater than zero"
            );
        }

        if (scheduledAt == null) {
            throw new IllegalArgumentException(
                    "Scheduled date and time are required"
            );
        }
    }
}