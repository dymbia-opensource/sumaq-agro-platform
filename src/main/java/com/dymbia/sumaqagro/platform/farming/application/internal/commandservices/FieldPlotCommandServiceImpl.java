package com.dymbia.sumaqagro.platform.farming.application.internal.commandservices;

import com.dymbia.sumaqagro.platform.farming.application.commandservices.FieldPlotCommandService;
import com.dymbia.sumaqagro.platform.farming.domain.model.aggregates.FieldPlot;
import com.dymbia.sumaqagro.platform.farming.domain.model.commands.RegisterFieldPlotCommand;
import com.dymbia.sumaqagro.platform.farming.infrastructure.persistence.jpa.FieldPlotRepository;
import com.dymbia.sumaqagro.platform.shared.application.error.ApplicationError;
import com.dymbia.sumaqagro.platform.shared.application.result.Result;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FieldPlotCommandServiceImpl implements FieldPlotCommandService {
    // Same limit as the web app
    private static final long SEED_PLAN_PLOT_QUOTA = 4;
    private final FieldPlotRepository fieldPlotRepository;

    public FieldPlotCommandServiceImpl(FieldPlotRepository fieldPlotRepository) {
        this.fieldPlotRepository = fieldPlotRepository;
    }

    @Override
    @Transactional
    public Result<FieldPlot, ApplicationError> handle(RegisterFieldPlotCommand command)
    {
        if(fieldPlotRepository.existsByOwnerUserIdAndName(command.ownerUserId(), command.name())) {
            return Result.failure(ApplicationError.conflict("field.plot.error.duplicateName"));
        }
        if (fieldPlotRepository.countByOwnerUserId(command.ownerUserId()) >= SEED_PLAN_PLOT_QUOTA)
            return Result.failure(ApplicationError.conflict("field.plot.error.quotaReached"));

        try {

            // saveAndFlush runs the INSERT now
            var fieldPlot = fieldPlotRepository.saveAndFlush(new FieldPlot(command));
            return Result.success(fieldPlot);
        } catch (DataIntegrityViolationException exception) {

            // Two requests with the same name at the same time: the unique constraint stops the second one
            return Result.failure(ApplicationError.conflict("field.plot.error.duplicateName"));
        }
    }
}
