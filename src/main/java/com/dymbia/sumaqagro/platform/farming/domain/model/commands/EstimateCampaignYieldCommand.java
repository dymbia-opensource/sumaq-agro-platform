package com.dymbia.sumaqagro.platform.farming.domain.model.commands;

import java.math.BigDecimal;

/**
 * Command to record the expected yield and, after the harvest, the actual yield of a campaign.
 *
 * @param campaignLedgerId the ledger of the campaign
 * @param expectedYield    the expected sacks or quintals
 * @param actualYield      the harvested sacks or quintals, optional until the harvest
 */
public record EstimateCampaignYieldCommand(Long campaignLedgerId, BigDecimal expectedYield, BigDecimal actualYield) {
    public EstimateCampaignYieldCommand {
        if (campaignLedgerId == null || campaignLedgerId <= 0) {
            throw new IllegalArgumentException("campaign.ledger.error.id.invalid");
        }
    }
}