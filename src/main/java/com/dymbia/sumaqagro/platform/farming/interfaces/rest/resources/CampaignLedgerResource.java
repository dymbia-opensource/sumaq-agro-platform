package com.dymbia.sumaqagro.platform.farming.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.List;

/**
 * Response body of the cost ledger of a crop campaign.
 *
 * @param id              the ledger id
 * @param cropCampaignId  the campaign that owns the ledger
 * @param yieldUnit       the yield unit ({@code SACK} or {@code QUINTAL})
 * @param expectedYield   the expected yield
 * @param actualYield     the actual yield, or null if the harvest is not done
 * @param frozen          whether the campaign finished and the ledger accepts no more changes
 * @param currency        the currency of every amount
 * @param totalInvestment the sum of every expense
 * @param breakevenPrice  the total investment divided by the reference yield
 * @param entries         the expenses
 */
@Schema(name = "CampaignLedgerResponse", description = "Cost ledger of a crop campaign")
public record CampaignLedgerResource(
        @Schema(description = "Campaign ledger identifier", example = "1")
        Long id,
        @Schema(description = "Crop campaign that owns the ledger", example = "1")
        Long cropCampaignId,
        @Schema(description = "Yield unit", example = "QUINTAL", allowableValues = {"SACK", "QUINTAL"})
        String yieldUnit,
        @Schema(description = "Expected yield", example = "120")
        BigDecimal expectedYield,
        @Schema(description = "Actual yield, null if the harvest is not done", example = "110", nullable = true)
        BigDecimal actualYield,
        @Schema(description = "Whether the ledger accepts no more changes", example = "false")
        boolean frozen,
        @Schema(description = "Currency of every amount", example = "PEN")
        String currency,
        @Schema(description = "Sum of every expense", example = "4500.00")
        BigDecimal totalInvestment,
        @Schema(description = "Total investment divided by the reference yield", example = "37.50")
        BigDecimal breakevenPrice,
        List<ExpenseEntryResource> entries) {
}
