package com.dymbia.sumaqagro.platform.farming.domain.model.commands;

/**
 * Command to freeze the cost ledger of a campaign that finished.
 *
 * @param cropCampaignId the campaign that finished
 */
public record FreezeCampaignLedgerCommand(Long cropCampaignId) {
    public FreezeCampaignLedgerCommand {
        if (cropCampaignId == null || cropCampaignId <= 0) {
            throw new IllegalArgumentException("crop.campaign.error.id.invalid");
        }
    }
}