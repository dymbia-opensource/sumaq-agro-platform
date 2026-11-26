package com.dymbia.sumaqagro.platform.crop.domain.model.queries;

/** Identifies a saved capture and the authenticated viewer, never an arbitrary remote URL. */
public record GetSatelliteObservationImageryQuery(Long plotId, Long observationId, Long requestedByUserId) {
    public GetSatelliteObservationImageryQuery {
        if (plotId == null || plotId <= 0 || observationId == null || observationId <= 0
                || requestedByUserId == null || requestedByUserId <= 0) {
            throw new IllegalArgumentException("Positive plot, observation and viewer IDs are required");
        }
    }
}
