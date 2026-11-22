package com.dymbia.sumaqagro.platform.farming.interfaces.rest.transform;

import com.dymbia.sumaqagro.platform.farming.domain.model.commands.RecordFieldFreightExpenseCommand;
import com.dymbia.sumaqagro.platform.farming.interfaces.rest.resources.RecordFieldFreightExpenseResource;

import java.util.Objects;

/**
 * Converts the record field freight expense request body into its command.
 */
public final class RecordFieldFreightExpenseCommandFromResourceAssembler {

    private RecordFieldFreightExpenseCommandFromResourceAssembler() {
    }

    /**
     * Builds the record field freight expense command.
     *
     * @param campaignLedgerId the ledger that receives the expense
     * @param resource         the request body
     * @return the command
     */
    public static RecordFieldFreightExpenseCommand toCommandFromResource(Long campaignLedgerId,
                                                                         RecordFieldFreightExpenseResource resource) {
        Objects.requireNonNull(resource, "Record field freight expense request body is required.");
        return new RecordFieldFreightExpenseCommand(campaignLedgerId, resource.route(), resource.cost(),
                resource.expenseDate(), resource.clientSyncId());
    }
}
