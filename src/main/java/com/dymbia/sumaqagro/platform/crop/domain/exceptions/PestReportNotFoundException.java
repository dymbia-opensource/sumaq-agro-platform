package com.dymbia.sumaqagro.platform.crop.domain.exceptions;

/**
 * Exception thrown when a pest report is not found.
 */
public class PestReportNotFoundException extends RuntimeException {

    /**
     * Constructs a new PestReportNotFoundException with the specified pest report ID.
     *
     * @param pestReportId the ID of the pest report that was not found
     */
    public PestReportNotFoundException(Long pestReportId) {
        super("Pest report with ID %s not found.".formatted(pestReportId));
    }
}