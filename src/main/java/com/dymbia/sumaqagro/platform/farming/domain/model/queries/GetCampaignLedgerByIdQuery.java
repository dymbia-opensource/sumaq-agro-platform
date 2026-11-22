package com.dymbia.sumaqagro.platform.farming.domain.model.queries;

/**
 * Query to get a cost ledger by its id.
 *
 * @param campaignLedgerId the ledger id
 */
public record GetCampaignLedgerByIdQuery(Long campaignLedgerId) {
    public GetCampaignLedgerByIdQuery {
        if (campaignLedgerId == null || campaignLedgerId <= 0) {
            throw new IllegalArgumentException("campaign.ledger.error.id.invalid");
        }
    }
}