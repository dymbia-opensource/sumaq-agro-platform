package com.dymbia.sumaqagro.platform.profiles.domain.model.queries;

/**
 * Query to get the agronomist currently assigned to a plot.
 *
 * @param plotId identity of the plot
 * @since 1.0
 */
public record GetAgronomistAssignedToPlotQuery(Long plotId) {
    public GetAgronomistAssignedToPlotQuery {
        if (plotId == null || plotId <= 0) {
            throw new IllegalArgumentException("Plot id must be a positive number");
        }
    }
}