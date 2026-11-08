package com.dymbia.sumaqagro.platform.crop.domain.model.commands;

/**
 * Command to mark a pest report as inspected.
 * This command encapsulates the necessary information to perform the inspection operation.
 */
public record MarkPestReportAsInspectedCommand(Long pestReportId) {

    /**
     * Constructs a new MarkPestReportAsInspectedCommand with the specified pest report ID.
     *
     * @param pestReportId The unique identifier of the pest report to be marked as inspected.
     *                     Must be a positive number.
     * @throws IllegalArgumentException if the pestReportId is null or not a positive number.
     */

    public MarkPestReportAsInspectedCommand {
        if (pestReportId == null || pestReportId <= 0) {
            throw new IllegalArgumentException("Pest report ID must be a positive number.");
        }
    }
}
