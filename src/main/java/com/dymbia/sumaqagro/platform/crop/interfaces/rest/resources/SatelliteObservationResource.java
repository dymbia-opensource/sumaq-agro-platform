package com.dymbia.sumaqagro.platform.crop.interfaces.rest.resources;

import java.time.Instant;

/**
 * API response containing a satellite observation for a plot.
 */
public record SatelliteObservationResource(
        Long id,
        Long plotId,
        Instant capturedAt, // date and time when the observation was captured
        String source, // name of the satellite or sensor that captured the observation
        Double ndviMean,
        Double ndwiMean,
        Double cloudCoveragePercent,
        Double surfaceTempKelvin
) {
}