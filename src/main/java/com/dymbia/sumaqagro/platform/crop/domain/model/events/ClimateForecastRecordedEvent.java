package com.dymbia.sumaqagro.platform.crop.domain.model.events;

import com.dymbia.sumaqagro.platform.crop.domain.model.valueobjects.Percentage;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.time.Instant;

/**
 * Carries the data of a newly recorded forecast without a generated persistence ID.
 */
@Getter
public class ClimateForecastRecordedEvent extends ApplicationEvent {

    private final String region;
    private final double temperatureCelsius;
    private final Percentage rainChance;
    private final Instant forecastDate;
    private final String forecastSource;

    /**
     * Creates an event describing a newly recorded climate forecast.
     *
     * @param source the object where the event originated
     * @param region the region for which the forecast was recorded
     * @param temperatureCelsius the forecasted temperature in Celsius
     * @param rainChance the forecasted chance of rain as a percentage
     * @param forecastDate the date and time of the forecast
     * @param forecastSource the source of the forecast information
     */
    public ClimateForecastRecordedEvent(
            Object source,
            String region,
            double temperatureCelsius,
            Percentage rainChance,
            Instant forecastDate,
            String forecastSource
    ) {
        super(source);
        this.region = region;
        this.temperatureCelsius = temperatureCelsius;
        this.rainChance = rainChance;
        this.forecastDate = forecastDate;
        this.forecastSource = forecastSource;
    }
}
