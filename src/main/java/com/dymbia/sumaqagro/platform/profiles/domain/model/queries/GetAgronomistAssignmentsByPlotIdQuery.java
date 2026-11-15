package com.dymbia.sumaqagro.platform.profiles.domain.model.queries;

/**
 * Query for every assignment of a plot (active or not). The frontend expects a list.
 */
public record GetAgronomistAssignmentsByPlotIdQuery(Long plotId) {
    public GetAgronomistAssignmentsByPlotIdQuery {
        if (plotId == null || plotId <= 0) {
            throw new IllegalArgumentException("Plot id must be a positive number");
        }
    }
}
