package com.dymbia.sumaqagro.platform.crop.domain.model.commands;

import java.time.LocalDate;

/**
 * Command to issue a technical prescription for a pest report.
 */
public record IssueTechnicalPrescriptionCommand(
        Long pestReportId,
        Long agronomistId,
        String product,
        String dosage,
        String instructions,
        LocalDate applicationDate
) {

    /**
     * Constructs a new IssueTechnicalPrescriptionCommand with validation.
     *
     * @param pestReportId   the ID of the pest report
     * @param agronomistId   the ID of the agronomist issuing the prescription
     * @param product        the product to be used in the prescription
     * @param dosage         the dosage instructions for the product
     * @param instructions   additional instructions for application
     * @param applicationDate the date when the prescription should be applied
     * @throws IllegalArgumentException if any required field is invalid or missing
     */
    public IssueTechnicalPrescriptionCommand {
        if (pestReportId == null || pestReportId <= 0) {
            throw new IllegalArgumentException(
                    "Pest report ID must be greater than zero"
            );
        }

        if (agronomistId == null || agronomistId <= 0) {
            throw new IllegalArgumentException(
                    "Agronomist ID must be greater than zero"
            );
        }

        if (applicationDate == null) {
            throw new IllegalArgumentException("Application date is required");
        }

        product = requiredText(product, "Product");
        dosage = requiredText(dosage, "Dosage");
        instructions = requiredText(instructions, "Instructions");
    }

    private static String requiredText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " is required");
        }

        return value.strip();
    }
}