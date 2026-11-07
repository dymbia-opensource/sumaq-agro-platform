package com.dymbia.sumaqagro.platform.crop.domain.model.queries;

/**
 * Query for retrieving agroclimatic alerts by region.
 *
 * @param region region name
 */

public record GetAgroclimaticAlertsByRegionQuery(String region) {

    public GetAgroclimaticAlertsByRegionQuery {
        if (region == null || region.isBlank()) {
            throw new IllegalArgumentException("Region is required");
        }

        region = region.trim();
    }
}