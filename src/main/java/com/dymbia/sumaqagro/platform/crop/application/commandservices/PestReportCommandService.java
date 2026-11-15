package com.dymbia.sumaqagro.platform.crop.application.commandservices;

import com.dymbia.sumaqagro.platform.crop.domain.model.commands.DeletePestReportCommand;
import com.dymbia.sumaqagro.platform.crop.domain.model.commands.MarkPestReportAsInspectedCommand;
import com.dymbia.sumaqagro.platform.crop.domain.model.commands.RecordPestReportCommand;
import com.dymbia.sumaqagro.platform.crop.domain.model.commands.ResolvePestReportCommand;
import com.dymbia.sumaqagro.platform.shared.application.result.ApplicationError;
import com.dymbia.sumaqagro.platform.shared.application.result.Result;

/**
 * Application service contract for pest report commands.
 */
public interface PestReportCommandService {

    /**
     * Handles the recording of a new pest report.
     *
     * @param command command containing pest report data
     * @return created pest report identifier or an application error
     * @see RecordPestReportCommand
     */
    Result<Long, ApplicationError> handle(RecordPestReportCommand command);

    /**
     * Handles the resolution of an existing pest report.
     *
     * @param command command containing pest report id and resolution data
     * @return resolved pest report identifier or an application error
     * @see ResolvePestReportCommand
     */
    Result<Long, ApplicationError> handle(ResolvePestReportCommand command);

    /**
     * Handles the deletion of an existing pest report.
     *
     * @param command command containing pest report id
     * @return deleted pest report identifier or an application error
     * @see DeletePestReportCommand
     */
    Result<Long, ApplicationError> handle(DeletePestReportCommand command);

    /**
     * Handles marking a pest report as inspected.
     *
     * @param command command containing pest report id
     * @return updated pest report identifier or an application error
     * @see MarkPestReportAsInspectedCommand
     */
    Result<Long, ApplicationError> handle(MarkPestReportAsInspectedCommand command);
}