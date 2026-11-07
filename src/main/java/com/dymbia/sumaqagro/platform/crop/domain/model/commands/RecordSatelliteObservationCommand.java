package com.dymbia.sumaqagro.platform.crop.domain.model.commands;

import com.dymbia.sumaqagro.platform.crop.domain.model.valueobjects.NdviIndex;
import com.dymbia.sumaqagro.platform.crop.domain.model.valueobjects.NdwiIndex;
import com.dymbia.sumaqagro.platform.crop.domain.model.valueobjects.Percentage;

import java.time.Instant;

/**
 * Command to record satellite measurements for a specific plot.
 * Unavailable measurements may be null.
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
}