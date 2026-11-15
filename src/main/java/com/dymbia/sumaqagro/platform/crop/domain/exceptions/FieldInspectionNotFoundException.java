package com.dymbia.sumaqagro.platform.crop.domain.exceptions;

/**
 * Exception thrown when a field inspection is not found.
 */
public class FieldInspectionNotFoundException extends RuntimeException {

    /**
     * Constructs a new FieldInspectionNotFoundException with a message indicating the missing field inspection ID.
     *
     * @param fieldInspectionId The ID of the field inspection that was not found.
     */
    public FieldInspectionNotFoundException(Long fieldInspectionId) {
        super("Field inspection with ID %s not found.".formatted(fieldInspectionId));
    }
}