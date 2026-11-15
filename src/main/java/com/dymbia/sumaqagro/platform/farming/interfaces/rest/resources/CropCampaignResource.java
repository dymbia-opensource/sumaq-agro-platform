package com.dymbia.sumaqagro.platform.farming.interfaces.rest.resources;

import java.time.LocalDate;

/**
 * Response body of a crop campaign.
 *
 * @param id                the campaign id
 * @param fieldPlotId       the plot where the campaign takes place
 * @param season            the season
 * @param cropType          the crop type
 * @param seedVariety       the seed variety
 * @param customSeedVariety whether the variety is not in the catalog
 * @param sowingDate        the sowing date, or null if the crop is not sown yet
 * @param status            the status ({@code IN_PROGRESS} or {@code FINISHED})
 */
public record CropCampaignResource(
        Long id,
        Long fieldPlotId,
        String season,
        String cropType,
        String seedVariety,
        boolean customSeedVariety,
        LocalDate sowingDate,
        String status) {
}
