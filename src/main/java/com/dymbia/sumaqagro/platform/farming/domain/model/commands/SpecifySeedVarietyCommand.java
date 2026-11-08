package com.dymbia.sumaqagro.platform.farming.domain.model.commands;

import com.dymbia.sumaqagro.platform.farming.domain.model.valueobjects.SeedVariety;

/**
 * Command to change the seed variety of a crop campaign.
 *
 * @param cropCampaignId the campaign id
 * @param seedVariety    the new seed variety
 * @param custom         whether the producer typed a variety that is not in the catalog
 * @since 1.0
 */
public record SpecifySeedVarietyCommand(Long cropCampaignId, SeedVariety seedVariety, boolean custom) {
    /**
     * Validates the command parameters.
     *
     * @throws IllegalArgumentException when the campaign id or the seed variety is invalid
     */
    public SpecifySeedVarietyCommand {
        if (cropCampaignId == null || cropCampaignId <= 0) {
            throw new IllegalArgumentException("crop.campaign.error.id.invalid");
        }
        if (seedVariety == null) {
            throw new IllegalArgumentException("crop.campaign.error.seedVariety.notBlank");
        }
    }
}
