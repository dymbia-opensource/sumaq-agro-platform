package com.dymbia.sumaqagro.platform.farming.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

/**
 * Request body to record the yield of a campaign (US-40).
 *
 * @param expectedYield the expected yield, greater than 0
 * @param actualYield   the actual yield after the harvest, optional
 */
@Schema(name = "EstimateCampaignYieldRequest", description = "Request payload to record the yield of a campaign")
public record EstimateCampaignYieldResource(
        @Schema(description = "Expected yield, greater than 0", example = "120")
        BigDecimal expectedYield,
        @Schema(description = "Actual yield after the harvest, optional", example = "110", nullable = true)
        BigDecimal actualYield) {
}
