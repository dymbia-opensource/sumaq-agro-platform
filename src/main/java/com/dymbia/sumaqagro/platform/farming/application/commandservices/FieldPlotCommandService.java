package com.dymbia.sumaqagro.platform.farming.application.commandservices;

import com.dymbia.sumaqagro.platform.farming.domain.model.commands.RegisterFieldPlotCommand;
import com.dymbia.sumaqagro.platform.shared.application.result.ApplicationError;
import com.dymbia.sumaqagro.platform.shared.application.result.Result;

// Use cases that change field plots
public interface FieldPlotCommandService {
    /**
     * Registers a new field plot for a producer.
     *
     * @param command the register field plot command
     * @return the id of the new field plot, or the error that stopped the registration
     */
    Result<Long, ApplicationError> handle(RegisterFieldPlotCommand command);
}
