package com.dymbia.sumaqagro.platform.farming.application.internal.eventhandlers;

import com.dymbia.sumaqagro.platform.farming.application.commandservices.FieldPlotCommandService;
import com.dymbia.sumaqagro.platform.farming.application.internal.outboundservices.monitoring.PolygonMonitoringService;
import com.dymbia.sumaqagro.platform.farming.domain.model.commands.LinkPlotPolygonCommand;
import com.dymbia.sumaqagro.platform.farming.domain.model.events.PlotBoundaryDelineatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/**
 * Policy P3: registers the delineated polygon in the monitoring provider and links it to the plot.
 * <p>
 * When the provider fails, the plot keeps its polygon and stays without monitoring.
 * </p>
 *
 * @since 1.0
 */
@Service
public class PlotBoundaryDelineatedEventHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(PlotBoundaryDelineatedEventHandler.class);

    private final PolygonMonitoringService polygonMonitoringService;
    private final FieldPlotCommandService fieldPlotCommandService;

    public PlotBoundaryDelineatedEventHandler(PolygonMonitoringService polygonMonitoringService,
                                              FieldPlotCommandService fieldPlotCommandService) {
        this.polygonMonitoringService = polygonMonitoringService;
        this.fieldPlotCommandService = fieldPlotCommandService;
    }

    /**
     * Removes the previous polygon, if any, and registers the new one.
     *
     * @param event the delineated boundary event
     */
    @EventListener
    public void on(PlotBoundaryDelineatedEvent event) {
        if (event.previousPolygonId() != null) {
            polygonMonitoringService.removePolygon(event.previousPolygonId());
        }
        polygonMonitoringService.registerPolygon(event.plotName(), event.boundary()).ifPresentOrElse(polygonId ->
                fieldPlotCommandService.handle(new LinkPlotPolygonCommand(event.fieldPlotId(), polygonId)), () ->
                LOGGER.warn("Plot {} stays without monitoring: its polygon was not registered", event.fieldPlotId()));
    }
}