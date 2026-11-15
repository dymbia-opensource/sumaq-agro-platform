package com.dymbia.sumaqagro.platform.crop.domain.model.commands;

/**
 * Command to delete a pest report record.
 * This command encapsulates the necessary information to perform the deletion operation.
 */

public record DeletePestReportCommand(Long pestReportId) {

    /**
     * Constructs a new DeletePestReportCommand with the specified pest report ID.
     *
     * @param pestReportId The unique identifier of the pest report to be deleted.
     *                     Must be a positive number.
     * @throws IllegalArgumentException if the pestReportId is null or not a positive number.
     */

    public DeletePestReportCommand {
        if (pestReportId == null || pestReportId <= 0) {
            throw new IllegalArgumentException("Pest report ID must be a positive number.");
        }
    }
}
