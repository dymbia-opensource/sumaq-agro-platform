package com.dymbia.sumaqagro.platform.farming.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Response body of one expense of a cost ledger.
 *
 * @param id           the expense id
 * @param category     the category ({@code INPUTS}, {@code LABOR} or {@code FREIGHT})
 * @param description  what was paid: the input, the activity or the route
 * @param quantity     the quantity; for labor it is workers by days
 * @param unitPrice    the price of one unit
 * @param total        quantity by unit price
 * @param expenseDate  the date of the expense
 * @param notes        optional notes
 * @param clientSyncId the id the offline client gave to the expense, if any
 */
@Schema(name = "ExpenseEntryResponse", description = "Expense of a cost ledger")
public record ExpenseEntryResource(
        @Schema(description = "Expense identifier", example = "1")
        Long id,
        @Schema(description = "Expense category", example = "INPUTS", allowableValues = {"INPUTS", "LABOR", "FREIGHT"})
        String category,
        @Schema(description = "Input, activity or route that was paid", example = "Urea")
        String description,
        @Schema(description = "Quantity; for labor it is workers by days", example = "10")
        BigDecimal quantity,
        @Schema(description = "Price of one unit", example = "95.50")
        BigDecimal unitPrice,
        @Schema(description = "Quantity by unit price", example = "955.00")
        BigDecimal total,
        @Schema(description = "Date of the expense", example = "2026-11-20")
        LocalDate expenseDate,
        @Schema(description = "Optional notes", nullable = true)
        String notes,
        @Schema(description = "Id the offline client gave to the expense", example = "a1b2c3", nullable = true)
        String clientSyncId) {
}
