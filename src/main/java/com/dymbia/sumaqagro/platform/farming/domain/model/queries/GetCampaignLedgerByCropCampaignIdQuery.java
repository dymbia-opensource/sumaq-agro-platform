package com.dymbia.sumaqagro.platform.farming.domain.model.queries;

/**
 * Query to get the cost ledger of a crop campaign.
 *
 * @param cropCampaignId the campaign id
 */
public record GetCampaignLedgerByCropCampaignIdQuery(Long cropCampaignId) {
    public GetCampaignLedgerByCropCampaignIdQuery {
        if (cropCampaignId == null || cropCampaignId <= 0) {
            throw new IllegalArgumentException("crop.campaign.error.id.invalid");
        }
    }
}
