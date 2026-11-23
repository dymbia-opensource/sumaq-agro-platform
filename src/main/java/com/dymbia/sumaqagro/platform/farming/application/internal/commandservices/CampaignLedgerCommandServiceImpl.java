package com.dymbia.sumaqagro.platform.farming.application.internal.commandservices;

import com.dymbia.sumaqagro.platform.farming.application.commandservices.CampaignLedgerCommandService;
import com.dymbia.sumaqagro.platform.farming.domain.model.aggregates.CampaignLedger;
import com.dymbia.sumaqagro.platform.farming.domain.model.commands.EstimateCampaignYieldCommand;
import com.dymbia.sumaqagro.platform.farming.domain.model.commands.FreezeCampaignLedgerCommand;
import com.dymbia.sumaqagro.platform.farming.domain.model.commands.OpenCampaignLedgerCommand;
import com.dymbia.sumaqagro.platform.farming.domain.model.commands.RecordDailyLaborExpenseCommand;
import com.dymbia.sumaqagro.platform.farming.domain.model.commands.RecordFieldFreightExpenseCommand;
import com.dymbia.sumaqagro.platform.farming.domain.model.commands.RecordInputExpenseCommand;
import com.dymbia.sumaqagro.platform.farming.domain.model.valueobjects.CropCampaignId;
import com.dymbia.sumaqagro.platform.farming.domain.repositories.CampaignLedgerRepository;
import com.dymbia.sumaqagro.platform.shared.application.result.ApplicationError;
import com.dymbia.sumaqagro.platform.shared.application.result.Result;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

/**
 * Command service implementation of the cost ledger aggregate.
 * <p>
 * Domain exceptions become application errors: {@link IllegalArgumentException} is a validation error (400)
 * and {@link IllegalStateException}, a frozen ledger, is a conflict (409).
 * </p>
 *
 * @since 1.1
 */
@Service
public class CampaignLedgerCommandServiceImpl implements CampaignLedgerCommandService {
    private static final String CAMPAIGN_LEDGER = "CampaignLedger";
    private static final String ALREADY_OPENED_REASON = "campaign.ledger.error.alreadyOpened";

    private final CampaignLedgerRepository campaignLedgerRepository;

    public CampaignLedgerCommandServiceImpl(CampaignLedgerRepository campaignLedgerRepository) {
        this.campaignLedgerRepository = campaignLedgerRepository;
    }

    @Override
    public Result<Long, ApplicationError> handle(OpenCampaignLedgerCommand command) {
        if (campaignLedgerRepository.existsByCropCampaignId(new CropCampaignId(command.cropCampaignId()))) {
            return Result.failure(ApplicationError.conflict(CAMPAIGN_LEDGER, ALREADY_OPENED_REASON));
        }
        try {
            var campaignLedger = campaignLedgerRepository.save(new CampaignLedger(command));
            return Result.success(campaignLedger.getId());
        } catch (IllegalArgumentException exception) {
            return Result.failure(ApplicationError.validationError(CAMPAIGN_LEDGER, exception.getMessage()));
        } catch (Exception exception) {
            return Result.failure(ApplicationError.unexpected("open-campaign-ledger", exception.getMessage()));
        }
    }

    @Override
    public Result<Long, ApplicationError> handle(RecordInputExpenseCommand command) {
        return changeLedger(command.campaignLedgerId(), ledger -> ledger.recordInputExpense(command.inputName(),
                command.quantity(), command.unitPrice(), command.expenseDate(), command.clientSyncId()));
    }

    @Override
    public Result<Long, ApplicationError> handle(RecordDailyLaborExpenseCommand command) {
        return changeLedger(command.campaignLedgerId(), ledger -> ledger.recordDailyLaborExpense(command.activity(),
                command.workers(), command.days(), command.dailyWage(), command.expenseDate(),
                command.clientSyncId()));
    }

    @Override
    public Result<Long, ApplicationError> handle(RecordFieldFreightExpenseCommand command) {
        return changeLedger(command.campaignLedgerId(), ledger -> ledger.recordFieldFreightExpense(command.route(),
                command.cost(), command.expenseDate(), command.clientSyncId()));
    }

    @Override
    public Result<Long, ApplicationError> handle(EstimateCampaignYieldCommand command) {
        return changeLedger(command.campaignLedgerId(),
                ledger -> ledger.estimateYield(command.expectedYield(), command.actualYield()));
    }

    @Override
    public Result<Long, ApplicationError> handle(FreezeCampaignLedgerCommand command) {
        var campaignLedger = campaignLedgerRepository.findByCropCampaignId(
                new CropCampaignId(command.cropCampaignId()));
        if (campaignLedger.isEmpty()) {
            return Result.failure(ApplicationError.notFound(CAMPAIGN_LEDGER, command.cropCampaignId().toString()));
        }
        if (campaignLedger.get().isFrozen()) {
            return Result.success(campaignLedger.get().getId());
        }
        return save(campaignLedger.get(), CampaignLedger::freeze, "freeze-campaign-ledger");
    }

    // Loads the ledger, applies the change of the aggregate and saves it
    private Result<Long, ApplicationError> changeLedger(Long campaignLedgerId, Consumer<CampaignLedger> change) {
        var campaignLedger = campaignLedgerRepository.findById(campaignLedgerId);
        if (campaignLedger.isEmpty()) {
            return Result.failure(ApplicationError.notFound(CAMPAIGN_LEDGER, campaignLedgerId.toString()));
        }
        return save(campaignLedger.get(), change, "change-campaign-ledger");
    }

    private Result<Long, ApplicationError> save(CampaignLedger campaignLedger, Consumer<CampaignLedger> change,
                                                String context) {
        try {
            change.accept(campaignLedger);
            campaignLedgerRepository.save(campaignLedger);
            return Result.success(campaignLedger.getId());
        } catch (IllegalArgumentException exception) {
            return Result.failure(ApplicationError.validationError(CAMPAIGN_LEDGER, exception.getMessage()));
        } catch (IllegalStateException exception) {
            return Result.failure(ApplicationError.conflict(CAMPAIGN_LEDGER, exception.getMessage()));
        } catch (Exception exception) {
            return Result.failure(ApplicationError.unexpected(context, exception.getMessage()));
        }
    }
}
