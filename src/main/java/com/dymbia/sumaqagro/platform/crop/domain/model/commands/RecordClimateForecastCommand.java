package com.dymbia.sumaqagro.platform.crop.domain.model.commands;

import com.dymbia.sumaqagro.platform.crop.domain.model.valueobjects.Percentage;

import java.time.Instant;

/**
 * Requests the recording of a weather forecast for a region and a specific time.
 */
public record RecordClimateForecastCommand(
        String region,
        double temperatureCelsius,
        Percentage rainChance,
        Instant forecastDate,
        String source
) {

    public RecordClimateForecastCommand {
        if (region == null || region.isBlank()) {
            throw new IllegalArgumentException("Region is required");
        }

        if (!Double.isFinite(temperatureCelsius) || temperatureCelsius < -273.15) {
            throw new IllegalArgumentException(
                    "Temperature must be finite and cannot be below absolute zero"
            );
        }

        if (rainChance == null) {
            throw new IllegalArgumentException("Rain chance is required");
        }

        if (forecastDate == null) {
            throw new IllegalArgumentException("Forecast date and time are required");
        }

        if (source == null || source.isBlank()) {
            throw new IllegalArgumentException("Forecast source is required");
        }

        region = region.strip();
        source = source.strip();
    }
}
