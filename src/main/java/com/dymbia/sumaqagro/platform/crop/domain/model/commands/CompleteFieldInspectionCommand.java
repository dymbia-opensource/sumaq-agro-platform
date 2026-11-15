package com.dymbia.sumaqagro.platform.crop.domain.model.commands;

/**
 * Requests the completion of an inspection with its findings.
 */
public record CompleteFieldInspectionCommand(Long fieldInspectionId, String findings) {

    /**
     * Constructs a new {@link CompleteFieldInspectionCommand} with the specified field inspection ID and findings.
     *
     * @param fieldInspectionId The ID of the field inspection to complete. Must be greater than zero.
     * @param findings          The findings of the inspection. Must contain at least 10 characters.
     * @throws IllegalArgumentException if the field inspection ID is not greater than zero or if the findings do not meet the minimum length requirement.
     */
    public CompleteFieldInspectionCommand {
        if (fieldInspectionId == null || fieldInspectionId <= 0) {
            throw new IllegalArgumentException("Field inspection ID must be greater than zero");
        }

        if (findings == null || findings.trim().length() < 10) {
            throw new IllegalArgumentException("Inspection findings must contain at least 10 characters");
        }

        findings = findings.trim();
    }
}