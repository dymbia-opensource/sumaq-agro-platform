package com.dymbia.sumaqagro.platform.crop.application.internal.eventhandlers;

import com.dymbia.sumaqagro.platform.crop.application.commandservices.SatelliteObservationCommandService;
import com.dymbia.sumaqagro.platform.crop.application.internal.outboundservices.acl.ExternalFarmingService;
import com.dymbia.sumaqagro.platform.crop.domain.model.commands.RequestSatelliteObservationImportCommand;
import com.dymbia.sumaqagro.platform.farming.domain.model.events.FieldPlotPolygonLinkedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/** Imports the first satellite observation after the monitoring linkage commits. */
@Service
public class FieldPlotPolygonLinkedEventHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(FieldPlotPolygonLinkedEventHandler.class);
    private final SatelliteObservationCommandService commandService;
    private final ExternalFarmingService farming;

    public FieldPlotPolygonLinkedEventHandler(SatelliteObservationCommandService commandService,
                                              ExternalFarmingService farming) {
        this.commandService = commandService;
        this.farming = farming;
    }

    /**
     * Searches the archive for the current polygon after its transaction completes.
     *
     * @param event the confirmed field plot linkage
     */
    @Async("applicationTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(FieldPlotPolygonLinkedEvent event) {
        try {
            if (farming.fetchMonitoringPolygonIdByPlotId(event.fieldPlotId())
                    .filter(event.polygonId()::equals).isEmpty()) {
                return;
            }
            var owner = farming.fetchOwnerUserIdByPlotId(event.fieldPlotId());
            if (owner.isEmpty()) {
                return;
            }
            var result = commandService.handle(new RequestSatelliteObservationImportCommand(event.fieldPlotId(), owner.get()));
            if (result.isFailure()) {
                LOGGER.warn("Automatic satellite import did not complete for plot {}", event.fieldPlotId());
            }
        } catch (RuntimeException exception) {
            LOGGER.warn("Automatic satellite import failed for plot {} ({})", event.fieldPlotId(),
                    exception.getClass().getSimpleName());
        }
    }
}
