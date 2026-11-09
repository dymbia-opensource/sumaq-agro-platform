package com.dymbia.sumaqagro.platform.farming.application.internal.commandservices;

import com.dymbia.sumaqagro.platform.farming.application.commandservices.FieldPlotCommandService;
import com.dymbia.sumaqagro.platform.farming.domain.model.aggregates.FieldPlot;
import com.dymbia.sumaqagro.platform.farming.domain.model.commands.DelineatePlotBoundaryCommand;
import com.dymbia.sumaqagro.platform.farming.domain.model.commands.RegisterFieldPlotCommand;
import com.dymbia.sumaqagro.platform.farming.domain.repositories.FieldPlotRepository;
import com.dymbia.sumaqagro.platform.shared.application.result.ApplicationError;
import com.dymbia.sumaqagro.platform.shared.application.result.Result;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

/**
 * Implementation of the field plot command service.
 * <p>
 * It checks the rules that need the database (repeated name and plot quota) before saving.
 * Like the commands of learning-center, it returns only the id: reading the plot is the job of the query service.
 * </p>
 * <p>
 * It is not {@code @Transactional}: a failed INSERT would mark the whole transaction as rollback-only,
 * and the commit at the end would turn the 409 conflict into a 500 error.
 * </p>
 */
@Service
public class FieldPlotCommandServiceImpl implements FieldPlotCommandService {
    // Same limit as the web app
    private static final long SEED_PLAN_PLOT_QUOTA = 4;
    private static final String DUPLICATE_NAME_REASON = "A plot with this name already exists for the producer.";

    private final FieldPlotRepository fieldPlotRepository;

    public FieldPlotCommandServiceImpl(FieldPlotRepository fieldPlotRepository) {
        this.fieldPlotRepository = fieldPlotRepository;
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
        if(fieldPlotRepository.existsByOwnerUserIdAndName(command.ownerUserId(), command.name())) {
            return Result.failure(ApplicationError.conflict("FieldPlot", DUPLICATE_NAME_REASON));
        }
        if (fieldPlotRepository.countByOwnerUserId(command.ownerUserId()) >= SEED_PLAN_PLOT_QUOTA)
            return Result.failure(ApplicationError.businessRuleViolation(
                    "plot-quota", "The producer reached the plot quota of the plan."));

        var fieldPlot = new FieldPlot(command);
        try {
            // save runs the INSERT now (the adapter uses saveAndFlush)
            fieldPlot = fieldPlotRepository.save(fieldPlot);
        } catch (DataIntegrityViolationException exception) {
            // Two requests with the same name at the same time: the unique constraint stops the second one
            return Result.failure(ApplicationError.conflict("FieldPlot", DUPLICATE_NAME_REASON));
        } catch (Exception exception) {
            return Result.failure(ApplicationError.unexpected("register-field-plot", exception.getMessage()));
        }
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
}
