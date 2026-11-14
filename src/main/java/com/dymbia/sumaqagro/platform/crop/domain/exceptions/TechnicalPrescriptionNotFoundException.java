package com.dymbia.sumaqagro.platform.crop.domain.exceptions;

/**
 * Exception thrown when a technical prescription is not found.
 */
public class TechnicalPrescriptionNotFoundException extends RuntimeException {

    /**
     * Constructs a new TechnicalPrescriptionNotFoundException with the specified technical prescription ID.
     *
     * @param technicalPrescriptionId the ID of the technical prescription that was not found
     */
    public TechnicalPrescriptionNotFoundException(Long technicalPrescriptionId) {
        super("Technical prescription with ID %s not found.".formatted(technicalPrescriptionId));
    }
}