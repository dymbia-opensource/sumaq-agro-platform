package com.dymbia.sumaqagro.platform.crop.domain.model.commands;

import com.dymbia.sumaqagro.platform.crop.domain.model.valueobjects.NdviIndex;
import com.dymbia.sumaqagro.platform.crop.domain.model.valueobjects.NdwiIndex;
import com.dymbia.sumaqagro.platform.crop.domain.model.valueobjects.Percentage;

import java.time.Instant;

/**
 * Requests the recording of satellite measurements for a plot.
 * Index and percentage ranges are validated by their value objects.
 */
public record RecordSatelliteObservationCommand(
        Long plotId,
        Instant capturedAt,
        String source,
        NdviIndex ndviMean,
        NdwiIndex ndwiMean,
        Percentage cloudCoveragePercent,
        Double surfaceTempKelvin
) {

    public RecordSatelliteObservationCommand {
        if (plotId == null || plotId <= 0) {
            throw new IllegalArgumentException("Plot ID must be greater than zero");
        }

        if (capturedAt == null) {
            throw new IllegalArgumentException("Capture date and time are required");
        }

        if (source == null || source.isBlank()) {
            throw new IllegalArgumentException("Observation source is required");
        }

        if (ndviMean == null && ndwiMean == null) {
            throw new IllegalArgumentException("At least one vegetation index is required");
        }

        if (cloudCoveragePercent == null) {
            throw new IllegalArgumentException("Cloud coverage percentage is required");
        }

        if (surfaceTempKelvin != null
                && (!Double.isFinite(surfaceTempKelvin) || surfaceTempKelvin < 0)) {
            throw new IllegalArgumentException(
                    "Surface temperature must be a finite, non-negative value in Kelvin"
            );
        }

        source = source.strip();
    }
}
