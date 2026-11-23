package com.dymbia.sumaqagro.platform.farming.domain.model.valueobjects;

/**
 * Identity of the crop campaign that owns a cost ledger
 *
 * @param cropCampaignId the crop campaign id
 * @since 1.0
 */
public record CropCampaignId(Long cropCampaignId) {

    public CropCampaignId {
        if (cropCampaignId == null || cropCampaignId <= 0) {
            throw new IllegalArgumentException("crop.campaign.error.id.invalid");
        }
    }
}
