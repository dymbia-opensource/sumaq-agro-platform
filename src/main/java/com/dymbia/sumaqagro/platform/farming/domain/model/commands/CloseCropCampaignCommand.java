package com.dymbia.sumaqagro.platform.farming.domain.model.commands;

/**
 * Command to finish a crop campaign.
 *
 * @param cropCampaignId the campaign id
 * @since 1.0
 */
public record CloseCropCampaignCommand(Long cropCampaignId) {
    /**
     * Validates the command parameters.
     *
     * @throws IllegalArgumentException when the campaign id is invalid
     */
    public CloseCropCampaignCommand {
        if (cropCampaignId == null || cropCampaignId <= 0) {
            throw new IllegalArgumentException("crop.campaign.error.id.invalid");
        }
    }
}
