package com.dymbia.sumaqagro.platform.crop.domain.model.queries;

/**
 * Query to retrieve a technical prescription by its ID.
 * This query encapsulates the necessary information to perform the retrieval operation.
 */
public record GetTechnicalPrescriptionByIdQuery(Long technicalPrescriptionId) {

    /**
     * Constructs a new GetTechnicalPrescriptionByIdQuery with the specified technical prescription ID.
     *
     * @param technicalPrescriptionId The unique identifier of the technical prescription to be retrieved.
     *                                Must be a positive number.
     * @throws IllegalArgumentException if the technicalPrescriptionId is null or not a positive number.
     */
    public GetTechnicalPrescriptionByIdQuery {
        if (technicalPrescriptionId == null || technicalPrescriptionId <= 0) {
            throw new IllegalArgumentException("Technical prescription ID must be greater than zero");
        }
    }
}