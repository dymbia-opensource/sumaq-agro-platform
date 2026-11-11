package com.dymbia.sumaqagro.platform.crop.domain.model.queries;

/**
 * Requests the satellite observation history of a plot.
 */
public record GetSatelliteObservationsByPlotIdQuery(Long plotId) {

    public GetSatelliteObservationsByPlotIdQuery {
        if (plotId == null || plotId <= 0) {
            throw new IllegalArgumentException("Plot ID must be greater than zero");
        }
    }
}
