package com.dymbia.sumaqagro.platform.crop.domain.model.commands;

/**
 * Command to resolve a pest report.
 * This command encapsulates the necessary information to perform the resolution operation.
 */

public record ResolvePestReportCommand(Long pestReportId) {

    /**
     * Constructs a new ResolvePestReportCommand with the specified pest report ID.
     *
     * @param pestReportId The unique identifier of the pest report to be resolved.
     *                     Must be a positive number.
     * @throws IllegalArgumentException if the pestReportId is null or not a positive number.
     */

    public ResolvePestReportCommand {
        if (pestReportId == null || pestReportId <= 0) {
            throw new IllegalArgumentException("Pest report ID must be a positive number.");
        }
    }
}
