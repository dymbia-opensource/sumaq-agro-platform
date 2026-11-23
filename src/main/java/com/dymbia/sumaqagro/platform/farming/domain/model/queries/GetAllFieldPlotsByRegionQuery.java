package com.dymbia.sumaqagro.platform.farming.domain.model.queries;

/**
 * Query for every field plot of a region.
 *
 * @param region the region, compared without case and surrounding spaces
 * @since 1.1
 */
public record GetAllFieldPlotsByRegionQuery(String region) {

    public GetAllFieldPlotsByRegionQuery {
        if (region == null || region.isBlank()) {
            throw new IllegalArgumentException("field.plot.error.region.required");
        }
        region = region.strip();
    }
}
