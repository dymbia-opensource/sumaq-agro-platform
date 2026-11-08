package com.dymbia.sumaqagro.platform.profiles.application.commandservices;

import com.dymbia.sumaqagro.platform.profiles.domain.model.aggregates.AgronomistAssignment;
import com.dymbia.sumaqagro.platform.profiles.domain.model.commands.AssignAgronomistToPlotCommand;
import com.dymbia.sumaqagro.platform.shared.application.result.ApplicationError;
import com.dymbia.sumaqagro.platform.shared.application.result.Result;

/**
 * Command service for the AgronomistAssignment aggregate.
 *
 * @since 1.0
 */
public interface AgronomistAssignmentCommandService {

    /**
     * Assigns an agronomist to a plot.
     *
     * @param command the assignment command
     * @return Result containing the created AgronomistAssignment or an ApplicationError
     */
    Result<AgronomistAssignment, ApplicationError> handle(AssignAgronomistToPlotCommand command);
}