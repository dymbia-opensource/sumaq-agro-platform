package com.dymbia.sumaqagro.platform.farming.interfaces.rest.transform;

import com.dymbia.sumaqagro.platform.farming.domain.model.commands.RecordDailyLaborExpenseCommand;
import com.dymbia.sumaqagro.platform.farming.interfaces.rest.resources.RecordDailyLaborExpenseResource;

import java.util.Objects;

/**
 * Converts the record daily labor expense request body into its command.
 */
public final class RecordDailyLaborExpenseCommandFromResourceAssembler {

    private RecordDailyLaborExpenseCommandFromResourceAssembler() {
    }

    /**
     * Builds the record daily labor expense command.
     *
     * @param campaignLedgerId the ledger that receives the expense
     * @param resource         the request body
     * @return the command
     */
    public static RecordDailyLaborExpenseCommand toCommandFromResource(Long campaignLedgerId,
                                                                       RecordDailyLaborExpenseResource resource) {
        Objects.requireNonNull(resource, "Record daily labor expense request body is required.");
        return new RecordDailyLaborExpenseCommand(campaignLedgerId, resource.activity(), resource.workers(),
                resource.days(), resource.dailyWage(), resource.expenseDate(), resource.clientSyncId());
    }
}
