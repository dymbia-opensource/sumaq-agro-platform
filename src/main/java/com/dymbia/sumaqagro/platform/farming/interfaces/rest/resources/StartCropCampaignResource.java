package com.dymbia.sumaqagro.platform.farming.interfaces.rest.resources;

import java.time.LocalDate;

/**
 * Request body to start a crop campaign.
 *
 * @param fieldPlotId       the plot where the campaign takes place
 * @param season            the season, {@code YYYY-I} or {@code YYYY-II}
 * @param cropType          the crop type ({@code ANDEAN_POTATO} or {@code SPECIALTY_COFFEE})
 * @param seedVariety       the seed variety
 * @param customSeedVariety whether the producer typed a variety that is not in the catalog
 * @param sowingDate        the sowing date, optional
 */
public record StartCropCampaignResource(
        Long fieldPlotId,
        String season,
        String cropType,
        String seedVariety,
        boolean customSeedVariety,
        LocalDate sowingDate) {
}
