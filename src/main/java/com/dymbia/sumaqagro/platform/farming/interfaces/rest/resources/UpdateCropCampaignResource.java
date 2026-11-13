package com.dymbia.sumaqagro.platform.farming.interfaces.rest.resources;

import java.time.LocalDate;

/**
 * Request body to change some data of a crop campaign. Only the fields that are sent change.
 *
 * @param cropType          the crop type ({@code ANDEAN_POTATO} or {@code SPECIALTY_COFFEE}), optional
 * @param seedVariety       the seed variety, optional
 * @param customSeedVariety whether the seed variety is not in the catalog, used with the seed variety
 * @param sowingDate        the sowing date in {@code YYYY-MM-DD} format, optional
 */
public record UpdateCropCampaignResource(String cropType, String seedVariety, Boolean customSeedVariety,
                                         LocalDate sowingDate) {
}
