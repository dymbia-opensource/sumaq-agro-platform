package com.dymbia.sumaqagro.platform.crop.domain.exceptions;

import com.dymbia.sumaqagro.platform.crop.domain.model.valueobjects.PestReportStatus;

/**
 * Exception thrown when an invalid status transition is attempted for a pest report.
 */
public class InvalidPestReportStatusTransitionException extends RuntimeException {

    /**
     * Constructs a new InvalidPestReportStatusTransitionException with the specified current and target statuses.
     *
     * @param currentStatus the current status of the pest report
     * @param targetStatus  the target status to which the pest report is being transitioned
     */
    public InvalidPestReportStatusTransitionException( PestReportStatus currentStatus, PestReportStatus targetStatus) {
        super("Cannot change pest report status from %s to %s.".formatted(currentStatus, targetStatus));
    }
}