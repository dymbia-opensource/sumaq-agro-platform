package com.dymbia.sumaqagro.platform.crop.domain.exceptions;

/**
 * Exception thrown when an already applied prescription is applied again.
 */
public class TechnicalPrescriptionAlreadyAppliedException extends RuntimeException {

    /**
     * Constructs a new TechnicalPrescriptionAlreadyAppliedException with the specified technical prescription ID.
     *
     * @param technicalPrescriptionId the ID of the technical prescription that is already applied
     */
    public TechnicalPrescriptionAlreadyAppliedException(Long technicalPrescriptionId) {
        super("Technical prescription with ID %s is already applied.".formatted(technicalPrescriptionId));
    }
}