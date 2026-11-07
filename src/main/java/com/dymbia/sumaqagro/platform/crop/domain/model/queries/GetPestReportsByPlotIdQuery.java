package com.dymbia.sumaqagro.platform.crop.domain.model.queries;

/**
 * Query for retrieving pest reports by plot identifier.
 *
 * @param plotId plot identifier
 */

public record GetPestReportsByPlotIdQuery(Long plotId) {

    public GetPestReportsByPlotIdQuery {
        if (plotId == null || plotId <= 0) {
            throw new IllegalArgumentException("Plot ID must be greater than zero");
        }
    }
}