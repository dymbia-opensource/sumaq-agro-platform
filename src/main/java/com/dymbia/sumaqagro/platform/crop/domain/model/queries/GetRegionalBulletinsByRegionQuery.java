package com.dymbia.sumaqagro.platform.crop.domain.model.queries;

/**
 * Requests published bulletins for a region.
 */
public record GetRegionalBulletinsByRegionQuery(String region) {

    public GetRegionalBulletinsByRegionQuery {
        if (region == null || region.isBlank()) {
            throw new IllegalArgumentException("Region is required");
        }

        region = region.strip();
    }
}