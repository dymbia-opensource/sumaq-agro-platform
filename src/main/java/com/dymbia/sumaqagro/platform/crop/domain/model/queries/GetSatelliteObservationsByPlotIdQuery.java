package com.dymbia.sumaqagro.platform.crop.domain.model.queries;

/**
 * Query for retrieving satellite observations by plot identifier.
 *
 * @param plotId plot identifier
 */

public record GetSatelliteObservationsByPlotIdQuery(Long plotId) {

    public GetSatelliteObservationsByPlotIdQuery {
        if (plotId == null || plotId <= 0) {
            throw new IllegalArgumentException(
                    "Plot ID must be greater than zero"
            );
        }
    }
}