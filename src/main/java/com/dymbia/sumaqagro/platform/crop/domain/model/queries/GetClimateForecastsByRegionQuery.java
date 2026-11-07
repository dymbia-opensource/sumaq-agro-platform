package com.dymbia.sumaqagro.platform.crop.domain.model.queries;

/**
 * Query for retrieving climate forecasts by region.
 *
 * @param region region name
 */

public record GetClimateForecastsByRegionQuery(String region) {

    public GetClimateForecastsByRegionQuery {
        if (region == null || region.isBlank()) {
            throw new IllegalArgumentException("Region is required");
        }

        region = region.trim();
    }
}