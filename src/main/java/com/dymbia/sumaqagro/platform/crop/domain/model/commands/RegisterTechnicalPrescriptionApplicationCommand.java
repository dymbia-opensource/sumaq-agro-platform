package com.dymbia.sumaqagro.platform.crop.domain.model.commands;

/**
 * Command to register the application of a technical prescription.
 * This command encapsulates the necessary information to perform the registration operation.
 */
public record RegisterTechnicalPrescriptionApplicationCommand(Long technicalPrescriptionId) {

    /**
     * Constructs a new RegisterTechnicalPrescriptionApplicationCommand with the specified technical prescription ID.
     *
     * @param technicalPrescriptionId The unique identifier of the technical prescription to be registered.
     *                                Must be a positive number.
     * @throws IllegalArgumentException if the technicalPrescriptionId is null or not a positive number.
     */
    public RegisterTechnicalPrescriptionApplicationCommand {
        if (technicalPrescriptionId == null || technicalPrescriptionId <= 0) {
            throw new IllegalArgumentException(
                    "Technical prescription ID must be greater than zero"
            );
        }
    }
}