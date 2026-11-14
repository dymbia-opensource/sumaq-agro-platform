package com.dymbia.sumaqagro.platform.crop.domain.model.commands;

import java.time.Instant;

/**
 * Requests the scheduling of an inspection for a pest report.
 */
public record ScheduleFieldInspectionCommand(Long pestReportId, Long agronomistId, Instant scheduledAt) {

    /**
     * Constructs a new ScheduleFieldInspectionCommand.
     *
     * @param pestReportId The ID of the pest report to be inspected.
     * @param agronomistId The ID of the agronomist who will perform the inspection.
     * @param scheduledAt  The date and time when the inspection is scheduled.
     * @throws IllegalArgumentException if any of the parameters are invalid.
     */
    public ScheduleFieldInspectionCommand {
        if (pestReportId == null || pestReportId <= 0) {
            throw new IllegalArgumentException("Pest report ID must be greater than zero");
        }

        if (agronomistId == null || agronomistId <= 0) {
            throw new IllegalArgumentException("Agronomist ID must be greater than zero");
        }

        if (scheduledAt == null) {
            throw new IllegalArgumentException("Scheduled date and time are required");
        }
    }
}