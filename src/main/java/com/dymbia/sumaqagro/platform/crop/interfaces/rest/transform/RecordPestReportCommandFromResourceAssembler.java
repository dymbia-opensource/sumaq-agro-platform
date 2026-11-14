package com.dymbia.sumaqagro.platform.crop.interfaces.rest.transform;

import com.dymbia.sumaqagro.platform.crop.domain.model.commands.RecordPestReportCommand;
import com.dymbia.sumaqagro.platform.crop.interfaces.rest.resources.RecordPestReportResource;

/**
 * Converts an API request resource into a pest report command.
 */
public class RecordPestReportCommandFromResourceAssembler {

    /**
     * Converts a RecordPestReportResource to a RecordPestReportCommand.
     *
     * @param resource The {@link RecordPestReportResource} to convert.
     * @return The {@link RecordPestReportCommand} that results from the conversion.
     */
    public static RecordPestReportCommand toCommandFromResource(RecordPestReportResource resource) {
        return new RecordPestReportCommand(
                resource.plotId(),
                resource.reporterId(),
                resource.description(),
                resource.severity()
        );
    }
}