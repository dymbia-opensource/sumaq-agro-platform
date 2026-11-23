package com.dymbia.sumaqagro.platform.farming.interfaces.rest.transform;

import com.dymbia.sumaqagro.platform.farming.domain.model.aggregates.CampaignLedger;
import com.dymbia.sumaqagro.platform.farming.domain.model.entities.ExpenseEntry;
import com.dymbia.sumaqagro.platform.farming.interfaces.rest.resources.CampaignLedgerResource;
import com.dymbia.sumaqagro.platform.farming.interfaces.rest.resources.ExpenseEntryResource;

import java.util.Objects;

/**
 * Converts a cost ledger aggregate, with its expenses, into its response body.
 */
public final class CampaignLedgerResourceFromEntityAssembler {

    private CampaignLedgerResourceFromEntityAssembler() {
    }

    /**
     * Builds the cost ledger response body.
     *
     * @param entity the cost ledger
     * @return the response body
     */
    public static CampaignLedgerResource toResourceFromEntity(CampaignLedger entity) {
        Objects.requireNonNull(entity, "Campaign ledger is required.");
        return new CampaignLedgerResource(
                entity.getId(),
                entity.getCropCampaignId().cropCampaignId(),
                entity.getYieldUnit().name(),
                entity.getExpectedYield(),
                entity.getActualYield(),
                entity.isFrozen(),
                entity.getCurrency(),
                entity.totalInvestment().amount(),
                entity.breakevenPrice().amount(),
                entity.getEntries().stream()
                        .map(CampaignLedgerResourceFromEntityAssembler::toResourceFromEntity).toList());
    }

    private static ExpenseEntryResource toResourceFromEntity(ExpenseEntry entry) {
        return new ExpenseEntryResource(
                entry.getId(),
                entry.getCategory().name(),
                entry.getDescription(),
                entry.getQuantity(),
                entry.getUnitPrice().amount(),
                entry.total().amount(),
                entry.getExpenseDate(),
                entry.getNotes(),
                entry.getClientSyncId());
    }
}
