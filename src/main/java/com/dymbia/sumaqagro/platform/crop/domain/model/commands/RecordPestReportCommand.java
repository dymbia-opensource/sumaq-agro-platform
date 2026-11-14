package com.dymbia.sumaqagro.platform.crop.domain.model.commands;

import com.dymbia.sumaqagro.platform.crop.domain.model.valueobjects.PestReportSeverity;

/**
 * Command to record an agricultural incident for a specific plot.
 */
public record RecordPestReportCommand(Long plotId, Long reporterId, String description, PestReportSeverity severity) {

    public RecordPestReportCommand {
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
    }
}
