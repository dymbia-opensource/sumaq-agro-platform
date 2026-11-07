package com.dymbia.sumaqagro.platform.crop.domain.model.commands;

import com.dymbia.sumaqagro.platform.crop.domain.model.valueobjects.Percentage;

import java.time.Instant;

/**
 * Command to record a regional weather forecast.
 * Temperature is expressed in degrees Celsius.
 * Rain chance is expressed as a percentage.
 */
public record RecordClimateForecastCommand(
        String region,
        double temperature,
        Percentage rainChance,
        Instant forecastDate,
        String source
) {
}