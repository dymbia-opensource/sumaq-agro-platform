package com.dymbia.sumaqagro.platform.crop.application.internal.eventhandlers;

import com.dymbia.sumaqagro.platform.crop.application.commandservices.PestReportCommandService;
import com.dymbia.sumaqagro.platform.crop.domain.model.commands.MarkPestReportAsInspectedCommand;
import com.dymbia.sumaqagro.platform.crop.domain.model.events.FieldInspectionCompletedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/**
 * Reacts to inspection completion by requesting the associated
 * pest report status update.
 */
@Service
public class FieldInspectionCompletedEventHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(FieldInspectionCompletedEventHandler.class);

    private final PestReportCommandService pestReportCommandService;

    public FieldInspectionCompletedEventHandler(PestReportCommandService pestReportCommandService) {
        this.pestReportCommandService = pestReportCommandService;
    }

    /**
     * Handles the {@link FieldInspectionCompletedEvent}.
     *
     * <p>When a field inspection is completed, this handler automatically
     * marks the associated pest report as inspected. This ensures that the
     * pest report status is synchronized with the inspection status.</p>
     *
     * @param event the {@link FieldInspectionCompletedEvent} published by the crop domain
     */
    @EventListener
    public void on(FieldInspectionCompletedEvent event) {
        var command = new MarkPestReportAsInspectedCommand(
                event.getPestReportId()
        );

        var result = pestReportCommandService.handle(command);

        if (result.isFailure()) {
            LOGGER.error(
                    "Inspection {} completed, but pest report {} status "
                            + "could not be synchronized. Result: {}",
                    event.getFieldInspectionId(),
                    event.getPestReportId(),
                    result
            );
            return;
        }

        LOGGER.info(
                "Inspection {} completed; pest report {} marked as inspected",
                event.getFieldInspectionId(),
                event.getPestReportId()
        );
    }
}