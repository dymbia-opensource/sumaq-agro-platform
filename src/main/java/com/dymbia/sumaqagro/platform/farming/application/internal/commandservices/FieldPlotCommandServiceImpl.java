package com.dymbia.sumaqagro.platform.farming.application.internal.commandservices;

import com.dymbia.sumaqagro.platform.farming.application.commandservices.FieldPlotCommandService;
import com.dymbia.sumaqagro.platform.farming.domain.model.aggregates.FieldPlot;
import com.dymbia.sumaqagro.platform.farming.domain.model.commands.RegisterFieldPlotCommand;
import com.dymbia.sumaqagro.platform.farming.domain.repositories.FieldPlotRepository;
import com.dymbia.sumaqagro.platform.shared.application.result.ApplicationError;
import com.dymbia.sumaqagro.platform.shared.application.result.Result;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of the field plot command service.
 * <p>
 * It checks the rules that need the database (repeated name and plot quota) before saving.
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
    @Transactional
    public Result<FieldPlot, ApplicationError> handle(RegisterFieldPlotCommand command)
    {
        if(fieldPlotRepository.existsByOwnerUserIdAndName(command.ownerUserId(), command.name())) {
            return Result.failure(ApplicationError.conflict("FieldPlot", DUPLICATE_NAME_REASON));
        }
        if (fieldPlotRepository.countByOwnerUserId(command.ownerUserId()) >= SEED_PLAN_PLOT_QUOTA)
            return Result.failure(ApplicationError.businessRuleViolation(
                    "plot-quota", "The producer reached the plot quota of the plan."));

        try {

            // save runs the INSERT now (the adapter uses saveAndFlush)
            var fieldPlot = fieldPlotRepository.save(new FieldPlot(command));
            return Result.success(fieldPlot);
        } catch (DataIntegrityViolationException exception) {

            // Two requests with the same name at the same time: the unique constraint stops the second one
            return Result.failure(ApplicationError.conflict("FieldPlot", DUPLICATE_NAME_REASON));
        }
    }
}
