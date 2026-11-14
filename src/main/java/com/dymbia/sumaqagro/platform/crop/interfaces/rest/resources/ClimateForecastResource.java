package com.dymbia.sumaqagro.platform.crop.interfaces.rest.resources;

import java.time.Instant;

/**
 * API response containing a climate forecast for a region.
 */
public record ClimateForecastResource(
        Long id,
        String region,
        double temperatureCelsius,
        double rainChance,
        Instant forecastDate,
        String source
) {
}