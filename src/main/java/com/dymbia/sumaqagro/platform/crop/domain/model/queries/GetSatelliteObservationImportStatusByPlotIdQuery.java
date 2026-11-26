package com.dymbia.sumaqagro.platform.crop.domain.model.queries;

/**
 * Requests the current satellite observation import status of a field plot.
 *
 * @param plotId the ID of the field plot whose import status is requested
 * @since 1.1.0
 */
public record GetSatelliteObservationImportStatusByPlotIdQuery(Long plotId) {

    public GetSatelliteObservationImportStatusByPlotIdQuery {
        if (plotId == null || plotId <= 0) {
            throw new IllegalArgumentException("field.plot.error.id.invalid");
        }
    }
}
