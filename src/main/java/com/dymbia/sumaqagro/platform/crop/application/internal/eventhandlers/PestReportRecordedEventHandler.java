package com.dymbia.sumaqagro.platform.crop.application.internal.eventhandlers;

import com.dymbia.sumaqagro.platform.crop.domain.model.events.PestReportRecordedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/**
 * Event handler for the PestReportRecordedEvent.
 */
@Service
public class PestReportRecordedEventHandler {

    /**
     * Logger for the PestReportRecordedEventHandler.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(PestReportRecordedEventHandler.class);

    /**
     * Handles the PestReportRecordedEvent.
     * <p>
     *     Logs the details of the recorded pest report.
     * </p>
     *
     * @param event The {@link PestReportRecordedEvent} event
     */
    @EventListener
    public void on(PestReportRecordedEvent event) {
        LOGGER.info(
                "Pest report recorded: reportId={}, plotId={}, reporterId={}",
                event.getPestReportId(),
                event.getPlotId(),
                event.getReporterId()
        );
    }
}