package com.dymbia.sumaqagro.platform.crop.domain.model.queries;

/**
 * Requests registered agroclimatic alerts for a region.
 */
public record GetAgroclimaticAlertsByRegionQuery(String region) {

    public GetAgroclimaticAlertsByRegionQuery {
        if (region == null || region.isBlank()) {
            throw new IllegalArgumentException("Region is required");
        }

        region = region.strip();
    }
}
