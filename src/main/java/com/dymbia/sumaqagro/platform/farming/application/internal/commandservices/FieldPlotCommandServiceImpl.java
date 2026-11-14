package com.dymbia.sumaqagro.platform.farming.application.internal.commandservices;

import com.dymbia.sumaqagro.platform.farming.application.commandservices.FieldPlotCommandService;
import com.dymbia.sumaqagro.platform.farming.domain.model.aggregates.FieldPlot;
import com.dymbia.sumaqagro.platform.farming.domain.model.commands.DelineatePlotBoundaryCommand;
import com.dymbia.sumaqagro.platform.farming.domain.model.commands.LinkPlotPolygonCommand;
import com.dymbia.sumaqagro.platform.farming.domain.model.commands.RecordSoilBaselineCommand;
import com.dymbia.sumaqagro.platform.farming.domain.model.commands.RegisterFieldPlotCommand;
import com.dymbia.sumaqagro.platform.farming.domain.repositories.FieldPlotRepository;
import com.dymbia.sumaqagro.platform.shared.application.result.ApplicationError;
import com.dymbia.sumaqagro.platform.shared.application.result.Result;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import com.dymbia.sumaqagro.platform.subscriptions.interfaces.acl.SubscriptionsContextFacade;
import com.dymbia.sumaqagro.platform.profiles.interfaces.acl.ProfilesContextFacade;

/**
 * Implementation of the field plot command service.
 * <p>
 * It checks the rules that need the database (repeated name and plot quota) before saving.
 * Like the commands of learning-center, it returns only the id: reading the plot is the job of the query service.
 * </p>
 */
@Service
public class FieldPlotCommandServiceImpl implements FieldPlotCommandService {
    private static final String DUPLICATE_NAME_REASON = "A plot with this name already exists for the producer.";

    private final FieldPlotRepository fieldPlotRepository;
    private final SubscriptionsContextFacade subscriptions;
    private final ProfilesContextFacade profiles;
    private final TransactionTemplate transaction;

    public FieldPlotCommandServiceImpl(
            FieldPlotRepository fieldPlotRepository,
            SubscriptionsContextFacade subscriptions,
            ProfilesContextFacade profiles,
            TransactionTemplate transaction
    ) {
        this.fieldPlotRepository = fieldPlotRepository;
        this.subscriptions = subscriptions;
        this.profiles = profiles;
        this.transaction = transaction;
    }

    /**
     * {@inheritDoc}
     * <p>
     * A repeated name is a {@code conflict} (409). A producer over the plot quota is a
     * {@code businessRuleViolation} (422).
     * </p>
     */
    @Override
    public Result<Long, ApplicationError> handle(RegisterFieldPlotCommand command) {
        try {
            return transaction.execute(status -> registerWithinTransaction(command));
        } catch (DataIntegrityViolationException exception) {
            return Result.failure(ApplicationError.conflict("FieldPlot", DUPLICATE_NAME_REASON));
        }
    }

    private Result<Long, ApplicationError> registerWithinTransaction(RegisterFieldPlotCommand command) {
        boolean cooperativePlot = command.cooperativeId() != null;
        var limit = cooperativePlot
                ? subscriptions.lockCooperativePlotLimit(command.ownerUserId())
                : subscriptions.lockSeedPlotLimit(command.ownerUserId());
        if (limit.isEmpty()) {
            return Result.failure(ApplicationError.businessRuleViolation(
                    "plot-plan-required", "A current subscription for this plot type is required."));
        }
        if (cooperativePlot && !profiles.managesCooperativeMember(
                command.ownerUserId(), command.cooperativeId(), command.cooperativeMemberId())) {
            return Result.failure(ApplicationError.notFound(
                    "CooperativeMember", command.cooperativeMemberId().toString()));
        }
        if (fieldPlotRepository.existsByOwnerUserIdAndName(command.ownerUserId(), command.name())) {
            return Result.failure(ApplicationError.conflict("FieldPlot", DUPLICATE_NAME_REASON));
        }
        long registered = cooperativePlot
                ? fieldPlotRepository.countByCooperativeId(command.cooperativeId())
                : fieldPlotRepository.countByOwnerUserId(command.ownerUserId());
        if (registered >= limit.get()) {
            return Result.failure(ApplicationError.businessRuleViolation(
                    "plot-quota", "The producer reached the plot quota of the plan."));
        }
        var fieldPlot = fieldPlotRepository.save(new FieldPlot(command));
        return Result.success(fieldPlot.getId());
    }

    /**
     * {@inheritDoc}
     * <p>
     * A missing plot is {@code notFound} (404). The aggregate rejects an invalid area (400).
     * </p>
     */
    @Override
    public Result<Long, ApplicationError> handle(DelineatePlotBoundaryCommand command) {
        var fieldPlot = fieldPlotRepository.findById(command.fieldPlotId());
        if (fieldPlot.isEmpty()) {
            return Result.failure(ApplicationError.notFound("FieldPlot", command.fieldPlotId().toString()));
        }
        fieldPlot.get().delineateBoundary(command.boundary());
        fieldPlotRepository.save(fieldPlot.get());
        return Result.success(command.fieldPlotId());
    }

    @Override
    public Result<Long, ApplicationError> handle(LinkPlotPolygonCommand command) {
        var fieldPlot = fieldPlotRepository.findById(command.fieldPlotId());
        if (fieldPlot.isEmpty()) {
            return Result.failure(ApplicationError.notFound("FieldPlot", command.fieldPlotId().toString()));
        }
        fieldPlot.get().linkPolygon(command.polygonId());
        fieldPlotRepository.save(fieldPlot.get());
        return Result.success(command.fieldPlotId());
    }

    /**
     * {@inheritDoc}
     * <p>
     * A missing plot is {@code notFound} (404). A new analysis replaces the previous one.
     * </p>
     */
    @Override
    public Result<Long, ApplicationError> handle(RecordSoilBaselineCommand command) {
        var fieldPlot = fieldPlotRepository.findById(command.fieldPlotId());
        if (fieldPlot.isEmpty()) {
            return Result.failure(ApplicationError.notFound("FieldPlot", command.fieldPlotId().toString()));
        }
        fieldPlot.get().recordSoilBaseline(command.soilBaseline());
        fieldPlotRepository.save(fieldPlot.get());
        return Result.success(command.fieldPlotId());
    }
}
