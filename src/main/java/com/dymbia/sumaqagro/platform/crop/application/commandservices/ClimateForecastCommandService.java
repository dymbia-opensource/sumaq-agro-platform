package com.dymbia.sumaqagro.platform.crop.application.commandservices;

import com.dymbia.sumaqagro.platform.crop.domain.model.commands.RecordClimateForecastCommand;
import com.dymbia.sumaqagro.platform.shared.application.result.ApplicationError;
import com.dymbia.sumaqagro.platform.shared.application.result.Result;

/**
 * Application service contract for climate forecast commands.
 */
public interface ClimateForecastCommandService {

    /**
     * Handles the recording of a new climate forecast.
     *
     * @param command command containing climate forecast data
     * @return created climate forecast identifier or an application error
     * @see RecordClimateForecastCommand
     */
    Result<Long, ApplicationError> handle(RecordClimateForecastCommand command);
}