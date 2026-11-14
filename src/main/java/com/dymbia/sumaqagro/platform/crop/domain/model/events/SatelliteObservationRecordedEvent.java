package com.dymbia.sumaqagro.platform.crop.domain.model.events;

import com.dymbia.sumaqagro.platform.crop.domain.model.valueobjects.NdviIndex;
import com.dymbia.sumaqagro.platform.crop.domain.model.valueobjects.NdwiIndex;
import com.dymbia.sumaqagro.platform.crop.domain.model.valueobjects.Percentage;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.time.Instant;

/**
 * Records the measurement snapshot of a newly registered observation.
 * The event carries capture data without requiring a generated persistence ID.
 */
@Getter
public class SatelliteObservationRecordedEvent extends ApplicationEvent {

    /**
     * The ID of the plot where the satellite observation was recorded.
     */
    private final Long plotId;
    private final Instant capturedAt;
    private final String observationSource;
    private final NdviIndex ndviMean;
    private final NdwiIndex ndwiMean;
    private final Percentage cloudCoveragePercent;
    private final Double surfaceTempKelvin;

    /**
     * Constructs a new SatelliteObservationRecordedEvent.
     *
     * @param source                the object that published the event
     * @param plotId                the ID of the plot where the observation was recorded
     * @param capturedAt            the timestamp when the observation was captured
     * @param observationSource     the source of the satellite observation
     * @param ndviMean              the mean NDVI index value
     * @param ndwiMean              the mean NDWI index value
     * @param cloudCoveragePercent  the percentage of cloud coverage during capture
     * @param surfaceTempKelvin     the surface temperature in Kelvin at capture time
     */
    public SatelliteObservationRecordedEvent(
            Object source,
            Long plotId,
            Instant capturedAt,
            String observationSource,
            NdviIndex ndviMean,
            NdwiIndex ndwiMean,
            Percentage cloudCoveragePercent,
            Double surfaceTempKelvin
    ) {
        super(source);
        this.plotId = plotId;
        this.capturedAt = capturedAt;
        this.observationSource = observationSource;
        this.ndviMean = ndviMean;
        this.ndwiMean = ndwiMean;
        this.cloudCoveragePercent = cloudCoveragePercent;
        this.surfaceTempKelvin = surfaceTempKelvin;
    }
}
