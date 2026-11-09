package com.dymbia.sumaqagro.platform.crop.application.commandservices;

import com.dymbia.sumaqagro.platform.crop.domain.model.commands.CompleteFieldInspectionCommand;
import com.dymbia.sumaqagro.platform.crop.domain.model.commands.ScheduleFieldInspectionCommand;
import com.dymbia.sumaqagro.platform.shared.application.result.ApplicationError;
import com.dymbia.sumaqagro.platform.shared.application.result.Result;

/**
 * Application service contract for field inspection commands.
 */
public interface FieldInspectionCommandService {

    /**
     * Schedules a new inspection.
     *
     * @param command pest report, agronomist and scheduling data
     * @return scheduled inspection identifier or an application error
     */
    Result<Long, ApplicationError> handle(ScheduleFieldInspectionCommand command);

    /**
     * Completes an existing inspection.
     *
     * @param command inspection identifier and findings
     * @return completed inspection identifier or an application error
     */
    Result<Long, ApplicationError> handle(CompleteFieldInspectionCommand command);
}