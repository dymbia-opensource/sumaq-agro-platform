package com.dymbia.sumaqagro.platform.crop.application.internal.eventhandlers;

import com.dymbia.sumaqagro.platform.crop.domain.model.events.SatelliteObservationRecordedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/**
 * Logs the capture details of newly recorded satellite observations.
 */
@Service
public class SatelliteObservationRecordedEventHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(SatelliteObservationRecordedEventHandler.class);

    /**
     * Handles the {@link SatelliteObservationRecordedEvent}.
     *
     * <p>When a satellite observation is recorded, this handler logs the
     * details of the observation, including the plot ID, capture timestamp,
     * and source of the observation.</p>
     *
     * @param event the {@link SatelliteObservationRecordedEvent} published by the crop domain
     */
    @EventListener
    public void on(SatelliteObservationRecordedEvent event) {
        LOGGER.info(
                "Satellite observation recorded: plotId={}, capturedAt={}, source={}",
                event.getPlotId(),
                event.getCapturedAt(),
                event.getObservationSource()
        );
    }
}
