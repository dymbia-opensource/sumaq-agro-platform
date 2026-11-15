package com.dymbia.sumaqagro.platform.crop.interfaces.rest.transform;

import com.dymbia.sumaqagro.platform.crop.domain.model.commands.ScheduleFieldInspectionCommand;
import com.dymbia.sumaqagro.platform.crop.interfaces.rest.resources.ScheduleFieldInspectionResource;

/**
 * Converts an API request into an inspection scheduling command.
 */
public class ScheduleFieldInspectionCommandFromResourceAssembler {

    /**
     * Converts a {@link ScheduleFieldInspectionResource} into a {@link ScheduleFieldInspectionCommand}.
     *
     * @param resource The resource to convert.
     * @return The resulting command.
     */
    public static ScheduleFieldInspectionCommand toCommandFromResource(ScheduleFieldInspectionResource resource) {
        return new ScheduleFieldInspectionCommand(
                resource.pestReportId(),
                resource.agronomistId(),
                resource.scheduledAt()
        );
    }
}
