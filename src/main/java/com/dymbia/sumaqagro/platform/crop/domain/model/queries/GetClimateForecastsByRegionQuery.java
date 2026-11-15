package com.dymbia.sumaqagro.platform.crop.domain.model.queries;

/**
 * Requests climate forecasts for a region.
 */
public record GetClimateForecastsByRegionQuery(String region) {

    public GetClimateForecastsByRegionQuery {
        if (region == null || region.isBlank()) {
            throw new IllegalArgumentException("Region is required");
        }

        region = region.strip();
    }
}
