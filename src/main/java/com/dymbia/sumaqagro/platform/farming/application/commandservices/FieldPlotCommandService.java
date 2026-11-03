package com.dymbia.sumaqagro.platform.farming.application.commandservices;

import com.dymbia.sumaqagro.platform.farming.domain.model.aggregates.FieldPlot;
import com.dymbia.sumaqagro.platform.farming.domain.model.commands.RegisterFieldPlotCommand;
import com.dymbia.sumaqagro.platform.shared.application.error.ApplicationError;
import com.dymbia.sumaqagro.platform.shared.application.result.Result;

// Use cases that change field plots
public interface FieldPlotCommandService {
    Result<FieldPlot, ApplicationError> handle(RegisterFieldPlotCommand command);
}
