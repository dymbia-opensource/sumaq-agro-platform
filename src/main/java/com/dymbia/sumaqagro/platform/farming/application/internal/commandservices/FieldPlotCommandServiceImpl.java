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

import java.util.function.Consumer;

/**
 * Implementation of the field plot command service.
 * <p>
 * It checks the rules that need the database (repeated name and plot quota) before saving.
 * Like the commands of learning-center, it returns only the id: reading the plot is the job of the query service.
 * </p>
 */
@Service
public class FieldPlotCommandServiceImpl implements FieldPlotCommandService {
    private static final String FIELD_PLOT = "FieldPlot";
    private static final String DUPLICATE_NAME_REASON = "field.plot.error.name.duplicated";
    private static final String PLAN_REQUIRED_REASON = "field.plot.error.plan.required";
    private static final String QUOTA_REACHED_REASON = "field.plot.error.quota.reached";

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
            return Result.failure(ApplicationError.conflict(FIELD_PLOT, DUPLICATE_NAME_REASON));
        } catch (IllegalArgumentException exception) {
            return Result.failure(ApplicationError.validationError(FIELD_PLOT, exception.getMessage()));
        } catch (Exception exception) {
            return Result.failure(ApplicationError.unexpected("register-field-plot", exception.getMessage()));
        }
    }

    private Result<Long, ApplicationError> registerWithinTransaction(RegisterFieldPlotCommand command) {
        boolean cooperativePlot = command.cooperativeId() != null;
        var limit = cooperativePlot
                ? subscriptions.lockCooperativePlotLimit(command.ownerUserId())
                : subscriptions.lockSeedPlotLimit(command.ownerUserId());
        if (limit.isEmpty()) {
            return Result.failure(ApplicationError.businessRuleViolation("plot-plan-required", PLAN_REQUIRED_REASON));
        }
        if (cooperativePlot && !profiles.managesCooperativeMember(
                command.ownerUserId(), command.cooperativeId(), command.cooperativeMemberId())) {
            return Result.failure(ApplicationError.notFound(
                    "CooperativeMember", command.cooperativeMemberId().toString()));
        }
        if (fieldPlotRepository.existsByOwnerUserIdAndName(command.ownerUserId(), command.name())) {
            return Result.failure(ApplicationError.conflict(FIELD_PLOT, DUPLICATE_NAME_REASON));
        }
        long registered = cooperativePlot
                ? fieldPlotRepository.countByCooperativeId(command.cooperativeId())
                : fieldPlotRepository.countByOwnerUserId(command.ownerUserId());
        if (registered >= limit.get()) {
            return Result.failure(ApplicationError.businessRuleViolation("plot-quota", QUOTA_REACHED_REASON));
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
        return changeFieldPlot(command.fieldPlotId(), "delineate-plot-boundary",
                fieldPlot -> fieldPlot.delineateBoundary(command.boundary()));
    }

    /**
     * {@inheritDoc}
     * <p>
     * A plot without a delineated boundary cannot be linked to a polygon: it is a {@code conflict} (409).
     * </p>
     */
    @Override
    public Result<Long, ApplicationError> handle(LinkPlotPolygonCommand command) {
        return changeFieldPlot(command.fieldPlotId(), "link-plot-polygon",
                fieldPlot -> fieldPlot.linkPolygon(command.polygonId()));
    }

    /**
     * {@inheritDoc}
     * <p>
     * A missing plot is {@code notFound} (404). A new analysis replaces the previous one.
     * </p>
     */
    @Override
    public Result<Long, ApplicationError> handle(RecordSoilBaselineCommand command) {
        return changeFieldPlot(command.fieldPlotId(), "record-soil-baseline",
                fieldPlot -> fieldPlot.recordSoilBaseline(command.soilBaseline()));
    }

    /**
     * Loads a plot, applies a change through the aggregate and saves it.
     * A missing plot is {@code notFound} (404), a value rejected by the aggregate is a
     * {@code validationError} (400) and a change that its current state does not allow is a {@code conflict} (409).
     *
     * @param fieldPlotId the plot id
     * @param context     the name of the operation, used when the error is unexpected
     * @param change      the change to apply through a method of the aggregate
     * @return the id of the plot, or the error that stopped it
     */
    private Result<Long, ApplicationError> changeFieldPlot(Long fieldPlotId, String context,
                                                           Consumer<FieldPlot> change) {
        var fieldPlot = fieldPlotRepository.findById(fieldPlotId);
        if (fieldPlot.isEmpty()) {
            return Result.failure(ApplicationError.notFound(FIELD_PLOT, fieldPlotId.toString()));
        }
        try {
            change.accept(fieldPlot.get());
            fieldPlotRepository.save(fieldPlot.get());
            return Result.success(fieldPlotId);
        } catch (IllegalArgumentException exception) {
            return Result.failure(ApplicationError.validationError(FIELD_PLOT, exception.getMessage()));
        } catch (IllegalStateException exception) {
            return Result.failure(ApplicationError.conflict(FIELD_PLOT, exception.getMessage()));
        } catch (Exception exception) {
            return Result.failure(ApplicationError.unexpected(context, exception.getMessage()));
        }
    }
}
