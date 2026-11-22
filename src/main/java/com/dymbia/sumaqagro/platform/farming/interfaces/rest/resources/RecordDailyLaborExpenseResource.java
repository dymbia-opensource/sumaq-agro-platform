package com.dymbia.sumaqagro.platform.farming.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Request body to record a daily labor expense (US-38).
 *
 * @param activity     the field activity: weeding, hilling, harvest
 * @param workers      the number of workers
 * @param days         the number of days
 * @param dailyWage    the wage of one worker for one day
 * @param expenseDate  the date of the expense
 * @param clientSyncId the id the offline client gave to the expense, optional
 */
@Schema(name = "RecordDailyLaborExpenseRequest", description = "Request payload to record a daily labor expense")
public record RecordDailyLaborExpenseResource(
        @Schema(description = "Field activity", example = "Aporque", maxLength = 120)
        String activity,
        @Schema(description = "Number of workers", example = "4", minimum = "1")
        Integer workers,
        @Schema(description = "Number of days", example = "2", minimum = "1")
        Integer days,
        @Schema(description = "Wage of one worker for one day", example = "50.00")
        BigDecimal dailyWage,
        @Schema(description = "Date of the expense, not in the future", example = "2026-11-20")
        LocalDate expenseDate,
        @Schema(description = "Id the offline client gave to the expense, optional", example = "a1b2c3", nullable = true)
        String clientSyncId) {
}
