package com.dymbia.sumaqagro.platform.crop.domain.model.queries;

/**
 * Query to retrieve technical prescriptions by their associated plot ID.
 * This query encapsulates the necessary information to perform the retrieval operation.
 */
public record GetTechnicalPrescriptionsByPlotIdQuery(Long plotId) {

    /**
     * Constructs a new GetTechnicalPrescriptionsByPlotIdQuery with the specified plot ID.
     *
     * @param plotId The unique identifier of the plot for which technical prescriptions are to be retrieved.
     *               Must be a positive number.
     * @throws IllegalArgumentException if the plotId is null or not a positive number.
     */
    public GetTechnicalPrescriptionsByPlotIdQuery {
        if (plotId == null || plotId <= 0) {
            throw new IllegalArgumentException("Plot ID must be greater than zero");
        }
    }
}