package com.dymbia.sumaqagro.platform.crop.domain.model.commands;

import com.dymbia.sumaqagro.platform.farming.domain.model.valueobjects.YieldUnit;

public record OpenCampaignLedgerCommand(Long cropCampaignId, YieldUnit yieldUnit) {
    public OpenCampaignLedgerCommand {
        if (cropCampaignId == null || cropCampaignId <= 0) {
            throw new IllegalArgumentException("crop.campaign.error.id.invalid");
        }
        if (yieldUnit == null) {
            throw new IllegalArgumentException("campaign.ledger.error.yieldUnit.required");
        }
    }
}
