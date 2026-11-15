package com.dymbia.sumaqagro.platform.profiles.application.commandservices;

import com.dymbia.sumaqagro.platform.profiles.domain.model.aggregates.AgronomistAssignment;
import com.dymbia.sumaqagro.platform.profiles.domain.model.commands.AssignAgronomistToPlotCommand;
import com.dymbia.sumaqagro.platform.profiles.domain.model.commands.ReassignAgronomistToPlotCommand;
import com.dymbia.sumaqagro.platform.shared.application.result.ApplicationError;
import com.dymbia.sumaqagro.platform.shared.application.result.Result;

/**
 * Write operations of the {@link AgronomistAssignment} aggregate.
 *
 * @since 1.0
 */
public interface AgronomistAssignmentCommandService {
    Result<AgronomistAssignment, ApplicationError> handle(AssignAgronomistToPlotCommand command);

    Result<AgronomistAssignment, ApplicationError> handle(ReassignAgronomistToPlotCommand command);
}
