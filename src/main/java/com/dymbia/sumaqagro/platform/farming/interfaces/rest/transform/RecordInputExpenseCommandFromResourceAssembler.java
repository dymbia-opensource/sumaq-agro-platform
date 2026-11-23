package com.dymbia.sumaqagro.platform.farming.interfaces.rest.transform;

import com.dymbia.sumaqagro.platform.farming.domain.model.commands.RecordInputExpenseCommand;
import com.dymbia.sumaqagro.platform.farming.interfaces.rest.resources.RecordInputExpenseResource;

import java.util.Objects;

/**
 * Converts the record input expense request body into its command.
 */
public final class RecordInputExpenseCommandFromResourceAssembler {

    private RecordInputExpenseCommandFromResourceAssembler() {
    }

    /**
     * Builds the record input expense command.
     *
     * @param campaignLedgerId the ledger that receives the expense
     * @param resource         the request body
     * @return the command
     */
    public static RecordInputExpenseCommand toCommandFromResource(Long campaignLedgerId,
                                                                  RecordInputExpenseResource resource) {
        Objects.requireNonNull(resource, "Record input expense request body is required.");
        return new RecordInputExpenseCommand(campaignLedgerId, resource.inputName(), resource.quantity(),
                resource.unitPrice(), resource.expenseDate(), resource.clientSyncId());
    }
}
