package com.dymbia.sumaqagro.platform.crop.application.internal.eventhandlers;

import com.dymbia.sumaqagro.platform.crop.domain.model.events.AgroclimaticAlertIssuedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/**
 * Logs newly issued agroclimatic alerts.
 */
@Service
public class AgroclimaticAlertIssuedEventHandler {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(AgroclimaticAlertIssuedEventHandler.class);

    @EventListener
    public void on(AgroclimaticAlertIssuedEvent event) {
        LOGGER.info(
                "Agroclimatic alert issued: region={}, severity={}, issuedAt={}, source={}",
                event.getRegion(),
                event.getSeverity(),
                event.getIssuedAt(),
                event.getAlertSource()
        );
    }
}
