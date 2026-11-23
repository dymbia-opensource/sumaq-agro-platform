package com.dymbia.sumaqagro.platform.farming.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Request body to record a field freight expense (US-39).
 *
 * @param route        the route of the freight
 * @param cost         the cost of the freight
 * @param expenseDate  the date of the expense
 * @param clientSyncId the id the offline client gave to the expense, optional
 */
@Schema(name = "RecordFieldFreightExpenseRequest", description = "Request payload to record a field freight expense")
public record RecordFieldFreightExpenseResource(
        @Schema(description = "Route of the freight", example = "Chacra - Acopio Huancayo", maxLength = 120)
        String route,
        @Schema(description = "Cost of the freight", example = "150.00")
        BigDecimal cost,
        @Schema(description = "Date of the expense, not in the future", example = "2026-11-20")
        LocalDate expenseDate,
        @Schema(description = "Id the offline client gave to the expense, optional", example = "a1b2c3", nullable = true)
        String clientSyncId) {
}
