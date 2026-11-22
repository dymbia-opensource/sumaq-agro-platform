package com.dymbia.sumaqagro.platform.crop.application.commandservices;

import com.dymbia.sumaqagro.platform.crop.domain.model.aggregates.ClimateForecast;
import com.dymbia.sumaqagro.platform.crop.domain.model.commands.ImportClimateForecastCommand;
import com.dymbia.sumaqagro.platform.crop.domain.model.commands.RecordClimateForecastCommand;
import com.dymbia.sumaqagro.platform.shared.application.result.ApplicationError;
import com.dymbia.sumaqagro.platform.shared.application.result.Result;

import java.util.List;

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

    /**
     * Imports the weather forecast of a plot from the monitoring provider. The forecast is recorded
     * as one climate forecast per day, and frost or water stress alerts are issued for the plot region.
     *
     * @param command the plot and the producer that asks for it
     * @return the recorded daily forecasts, or an application error
     * @see ImportClimateForecastCommand
     * @since 1.1
     */
    Result<List<ClimateForecast>, ApplicationError> handle(ImportClimateForecastCommand command);
}