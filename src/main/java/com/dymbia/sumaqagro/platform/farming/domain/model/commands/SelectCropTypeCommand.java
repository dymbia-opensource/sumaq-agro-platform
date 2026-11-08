package com.dymbia.sumaqagro.platform.farming.domain.model.commands;

import com.dymbia.sumaqagro.platform.farming.domain.model.valueobjects.CropType;

/**
 * Command to change the crop of a crop campaign.
 *
 * @param cropCampaignId the campaign id
 * @param cropType       the new crop type
 * @since 1.0
 */
public record SelectCropTypeCommand(Long cropCampaignId, CropType cropType) {
    /**
     * Validates the command parameters.
     *
     * @throws IllegalArgumentException when the campaign id or the crop type is invalid
     */
    public SelectCropTypeCommand {
        if (cropCampaignId == null || cropCampaignId <= 0) {
            throw new IllegalArgumentException("crop.campaign.error.id.invalid");
        }
        if (cropType == null) {
            throw new IllegalArgumentException("crop.campaign.error.cropType.required");
        }
    }
}
