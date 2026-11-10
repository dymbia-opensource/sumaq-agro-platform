package com.dymbia.sumaqagro.platform.farming.interfaces.rest.resources;

/**
 * Request body to change the seed variety of a campaign.
 *
 * @param seedVariety       the seed variety
 * @param customSeedVariety whether the producer typed a variety that is not in the catalog
 */
public record SpecifySeedVarietyResource(String seedVariety, boolean customSeedVariety) {
}
