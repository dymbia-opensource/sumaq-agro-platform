package com.dymbia.sumaqagro.platform.farming.application.commandservices;

import com.dymbia.sumaqagro.platform.farming.domain.model.commands.DelineatePlotBoundaryCommand;
import com.dymbia.sumaqagro.platform.farming.domain.model.commands.LinkPlotPolygonCommand;
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

    /**
     * Draws or redraws the polygon of a field plot.
     *
     * @param command the delineate plot boundary command
     * @return the id of the field plot, or the error that stopped the operation
     */
    Result<Long, ApplicationError> handle(DelineatePlotBoundaryCommand command);

    /**
     * Links a field plot to its polygon in the monitoring provider.
     *
     * @param command the link plot polygon command
     * @return the id of the field plot, or the error that stopped it
     */
    Result<Long, ApplicationError> handle(LinkPlotPolygonCommand command);
}
