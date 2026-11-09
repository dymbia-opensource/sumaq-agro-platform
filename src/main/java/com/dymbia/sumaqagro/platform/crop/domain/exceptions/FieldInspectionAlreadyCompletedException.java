package com.dymbia.sumaqagro.platform.crop.domain.exceptions;

/**
 * Exception thrown when a completed inspection is completed again.
 */
public class FieldInspectionAlreadyCompletedException extends RuntimeException {

    /**
     * Constructs a new FieldInspectionAlreadyCompletedException with the specified field inspection ID.
     *
     * @param fieldInspectionId The ID of the field inspection that is already completed.
     */
    public FieldInspectionAlreadyCompletedException(Long fieldInspectionId) {
        super("Field inspection with ID %s is already completed.".formatted(fieldInspectionId));
    }
}