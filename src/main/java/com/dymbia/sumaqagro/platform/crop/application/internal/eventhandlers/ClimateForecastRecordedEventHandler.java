package com.dymbia.sumaqagro.platform.crop.application.internal.eventhandlers;

import com.dymbia.sumaqagro.platform.crop.domain.model.events.ClimateForecastRecordedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/**
 * Event handler for the ClimateForecastRecordedEvent.
 */
@Service
public class ClimateForecastRecordedEventHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClimateForecastRecordedEventHandler.class);

    /**
     * Handles the ClimateForecastRecordedEvent.
     *
     * <p>Logs the details of the recorded climate forecast.</p>
     *
     * @param event The {@link ClimateForecastRecordedEvent} event
     */
    @EventListener
    public void on(ClimateForecastRecordedEvent event) {
        LOGGER.info(
                "Climate forecast recorded: region={}, forecastDate={}, source={}",
                event.getRegion(),
                event.getForecastDate(),
                event.getForecastSource()
        );
    }
}