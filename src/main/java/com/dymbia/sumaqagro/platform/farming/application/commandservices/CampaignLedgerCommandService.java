package com.dymbia.sumaqagro.platform.farming.application.commandservices;

import com.dymbia.sumaqagro.platform.farming.domain.model.commands.EstimateCampaignYieldCommand;
import com.dymbia.sumaqagro.platform.farming.domain.model.commands.FreezeCampaignLedgerCommand;
import com.dymbia.sumaqagro.platform.farming.domain.model.commands.OpenCampaignLedgerCommand;
import com.dymbia.sumaqagro.platform.farming.domain.model.commands.RecordDailyLaborExpenseCommand;
import com.dymbia.sumaqagro.platform.farming.domain.model.commands.RecordFieldFreightExpenseCommand;
import com.dymbia.sumaqagro.platform.farming.domain.model.commands.RecordInputExpenseCommand;
import com.dymbia.sumaqagro.platform.shared.application.result.ApplicationError;
import com.dymbia.sumaqagro.platform.shared.application.result.Result;

/**
 * Command service of the cost ledger aggregate.
 * <p>
 * Every command answers the id of the ledger it changed; the caller reads the ledger back with a query (CQRS).
 * </p>
 *
 * @since 1.1
 */
public interface CampaignLedgerCommandService {

    /**
     * Opens the cost ledger of a crop campaign (US-36). A campaign has only one ledger.
     *
     * @param command the campaign and its yield unit
     * @return the id of the new ledger, or a conflict when the campaign already has one
     */
    Result<Long, ApplicationError> handle(OpenCampaignLedgerCommand command);

    /**
     * Records an input expense: seeds, fertilizers, pesticides (US-37).
     *
     * @param command the ledger and the expense data
     * @return the id of the ledger
     */
    Result<Long, ApplicationError> handle(RecordInputExpenseCommand command);

    /**
     * Records a daily labor expense: workers by days by daily wage (US-38).
     *
     * @param command the ledger and the expense data
     * @return the id of the ledger
     */
    Result<Long, ApplicationError> handle(RecordDailyLaborExpenseCommand command);

    /**
     * Records a field freight expense (US-39).
     *
     * @param command the ledger and the expense data
     * @return the id of the ledger
     */
    Result<Long, ApplicationError> handle(RecordFieldFreightExpenseCommand command);

    /**
     * Records the expected and actual yield of the campaign (US-40).
     *
     * @param command the ledger and the yields
     * @return the id of the ledger
     */
    Result<Long, ApplicationError> handle(EstimateCampaignYieldCommand command);

    /**
     * Freezes the ledger of a finished campaign (US-35). Freezing twice is not an error.
     *
     * @param command the campaign whose ledger is frozen
     * @return the id of the ledger
     */
    Result<Long, ApplicationError> handle(FreezeCampaignLedgerCommand command);
}
