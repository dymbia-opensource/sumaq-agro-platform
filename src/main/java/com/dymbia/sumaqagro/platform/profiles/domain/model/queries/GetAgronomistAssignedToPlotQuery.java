package com.dymbia.sumaqagro.platform.profiles.domain.model.queries;

/**
 * Query for the active assignment of a plot (used by Crop Health through the facade).
 */
public record GetAgronomistAssignedToPlotQuery(Long plotId) {
    public GetAgronomistAssignedToPlotQuery {
        if (plotId == null || plotId <= 0) {
            throw new IllegalArgumentException("Plot id must be a positive number");
        }
    }
}
