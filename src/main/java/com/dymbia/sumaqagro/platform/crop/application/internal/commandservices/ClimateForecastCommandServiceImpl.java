package com.dymbia.sumaqagro.platform.crop.application.internal.commandservices;

import com.dymbia.sumaqagro.platform.crop.application.commandservices.ClimateForecastCommandService;
import com.dymbia.sumaqagro.platform.crop.domain.model.aggregates.ClimateForecast;
import com.dymbia.sumaqagro.platform.crop.domain.model.commands.RecordClimateForecastCommand;
import com.dymbia.sumaqagro.platform.crop.domain.repositories.ClimateForecastRepository;
import com.dymbia.sumaqagro.platform.shared.application.result.ApplicationError;
import com.dymbia.sumaqagro.platform.shared.application.result.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ClimateForecastCommandServiceImpl implements ClimateForecastCommandService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClimateForecastCommandServiceImpl.class);

    private final ClimateForecastRepository forecastRepository;

    public ClimateForecastCommandServiceImpl(ClimateForecastRepository forecastRepository) {
        this.forecastRepository = forecastRepository;
    }

    @Override
    public Result<Long, ApplicationError> handle(RecordClimateForecastCommand command) {
        try {
            var forecast = new ClimateForecast(command);
            var savedForecast = forecastRepository.save(forecast);

            return Result.success(savedForecast.getId());
        } catch (RuntimeException e) {
            LOGGER.error("Failed to record climate forecast", e);
            return Result.failure(ApplicationError.unexpected(
                    "record-climate-forecast",
                    "Could not record the climate forecast"
            ));
        }
    }
}