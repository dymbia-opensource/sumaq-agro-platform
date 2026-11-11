package com.dymbia.sumaqagro.platform.crop.domain.model.aggregates;

import com.dymbia.sumaqagro.platform.crop.domain.model.commands.RecordClimateForecastCommand;
import com.dymbia.sumaqagro.platform.crop.domain.model.events.ClimateForecastRecordedEvent;
import com.dymbia.sumaqagro.platform.crop.domain.model.valueobjects.Percentage;
import com.dymbia.sumaqagro.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;

import java.time.Instant;
import java.util.Objects;

/**
 * Aggregate root representing a forecast for a region at a specific time.
 */
@Getter
public class ClimateForecast extends AbstractDomainAggregateRoot<ClimateForecast> {

    private final Long id;
    private final String region;
    private final double temperatureCelsius;
    private final Percentage rainChance;
    private final Instant forecastDate;
    private final String source; // origin of the forecast information

    private ClimateForecast(Long id, RecordClimateForecastCommand command) {
        Objects.requireNonNull(command, "Command is required");

        this.id = id;
        this.region = command.region();
        this.temperatureCelsius = command.temperatureCelsius();
        this.rainChance = command.rainChance();
        this.forecastDate = command.forecastDate();
        this.source = command.source();
    }

    /**
     * Creates a forecast and registers the event for publication by the persistence adapter.
     */
    public ClimateForecast(RecordClimateForecastCommand command) {
        this(null, command);

        registerDomainEvent(new ClimateForecastRecordedEvent(
                this,
                this.region,
                this.temperatureCelsius,
                this.rainChance,
                this.forecastDate,
                this.source
        ));
    }


    /**
     * Reconstitutes a climate forecast from persisted data.
     *
     * @param id the unique identifier of the forecast
     * @param region the region for which the forecast was recorded
     * @param temperatureCelsius the forecasted temperature in Celsius
     * @param rainChance the forecasted chance of rain as a percentage
     * @param forecastDate the date and time of the forecast
     * @param source the source of the forecast information
     * @return a reconstituted ClimateForecast instance
     */
    public static ClimateForecast reconstitute(
            Long id,
            String region,
            double temperatureCelsius,
            Percentage rainChance,
            Instant forecastDate,
            String source
    ) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Climate forecast ID must be greater than zero");
        }

        var command = new RecordClimateForecastCommand(
                region,
                temperatureCelsius,
                rainChance,
                forecastDate,
                source
        );

        return new ClimateForecast(id, command);
    }
}
