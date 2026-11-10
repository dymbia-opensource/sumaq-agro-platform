package com.dymbia.sumaqagro.platform.farming.domain.model.queries;

/**
 * Query to get a crop campaign by its id.
 *
 * @param cropCampaignId the campaign id
 * @since 1.0
 */
public record GetCropCampaignByIdQuery(Long cropCampaignId) {
    public GetCropCampaignByIdQuery {
        if (cropCampaignId == null || cropCampaignId <= 0) {
            throw new IllegalArgumentException("crop.campaign.error.id.invalid");
        }
    }
}
