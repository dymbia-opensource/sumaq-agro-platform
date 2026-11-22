package com.dymbia.sumaqagro.platform.farming.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Request body to record an input expense (US-37).
 *
 * @param inputName    the input: seed, fertilizer, pesticide
 * @param quantity     the quantity bought
 * @param unitPrice    the price of one unit
 * @param expenseDate  the date of the expense
 * @param clientSyncId the id the offline client gave to the expense, optional
 */
@Schema(name = "RecordInputExpenseRequest", description = "Request payload to record an input expense")
public record RecordInputExpenseResource(
        @Schema(description = "Input that was bought", example = "Urea", maxLength = 120)
        String inputName,
        @Schema(description = "Quantity bought", example = "10")
        BigDecimal quantity,
        @Schema(description = "Price of one unit", example = "95.50")
        BigDecimal unitPrice,
        @Schema(description = "Date of the expense, not in the future", example = "2026-11-20")
        LocalDate expenseDate,
        @Schema(description = "Id the offline client gave to the expense, optional", example = "a1b2c3", nullable = true)
        String clientSyncId) {
}
