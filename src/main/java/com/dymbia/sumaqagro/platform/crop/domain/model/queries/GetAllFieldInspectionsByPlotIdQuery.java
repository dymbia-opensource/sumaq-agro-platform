package com.dymbia.sumaqagro.platform.crop.domain.model.queries;

/**
 * Requests the field inspections associated with a plot.
 */
public record GetAllFieldInspectionsByPlotIdQuery(Long plotId) {

    public GetAllFieldInspectionsByPlotIdQuery {
        if (plotId == null || plotId <= 0) {
            throw new IllegalArgumentException("Plot ID must be greater than zero");
        }
    }
}