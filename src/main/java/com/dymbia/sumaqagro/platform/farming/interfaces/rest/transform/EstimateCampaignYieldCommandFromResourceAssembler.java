package com.dymbia.sumaqagro.platform.farming.interfaces.rest.transform;

import com.dymbia.sumaqagro.platform.farming.domain.model.commands.EstimateCampaignYieldCommand;
import com.dymbia.sumaqagro.platform.farming.interfaces.rest.resources.EstimateCampaignYieldResource;

import java.util.Objects;

/**
 * Converts the estimate campaign yield request body into its command.
 */
public final class EstimateCampaignYieldCommandFromResourceAssembler {

    private EstimateCampaignYieldCommandFromResourceAssembler() {
    }

    /**
     * Builds the estimate campaign yield command.
     *
     * @param campaignLedgerId the ledger whose yield changes
     * @param resource         the request body
     * @return the command
     */
    public static EstimateCampaignYieldCommand toCommandFromResource(Long campaignLedgerId,
                                                                     EstimateCampaignYieldResource resource) {
        Objects.requireNonNull(resource, "Estimate campaign yield request body is required.");
        return new EstimateCampaignYieldCommand(campaignLedgerId, resource.expectedYield(), resource.actualYield());
    }
}
