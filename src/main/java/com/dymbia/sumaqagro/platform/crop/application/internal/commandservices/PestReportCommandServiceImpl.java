package com.dymbia.sumaqagro.platform.crop.application.internal.commandservices;

import com.dymbia.sumaqagro.platform.crop.application.commandservices.PestReportCommandService;
import com.dymbia.sumaqagro.platform.crop.domain.exceptions.InvalidPestReportStatusTransitionException;
import com.dymbia.sumaqagro.platform.crop.domain.model.aggregates.PestReport;
import com.dymbia.sumaqagro.platform.crop.domain.model.commands.DeletePestReportCommand;
import com.dymbia.sumaqagro.platform.crop.domain.model.commands.MarkPestReportAsInspectedCommand;
import com.dymbia.sumaqagro.platform.crop.domain.model.commands.RecordPestReportCommand;
import com.dymbia.sumaqagro.platform.crop.domain.model.commands.ResolvePestReportCommand;
import com.dymbia.sumaqagro.platform.crop.domain.repositories.PestReportRepository;
import com.dymbia.sumaqagro.platform.shared.application.result.ApplicationError;
import com.dymbia.sumaqagro.platform.shared.application.result.Result;
import org.springframework.stereotype.Service;

/**
 * Application service that executes pest report commands.
 */
@Service
public class PestReportCommandServiceImpl implements PestReportCommandService {

    private final PestReportRepository pestReportRepository;

    public PestReportCommandServiceImpl(PestReportRepository pestReportRepository) {
        this.pestReportRepository = pestReportRepository;
    }

    @Override
    public Result<Long, ApplicationError> handle(RecordPestReportCommand command) {
        try {
            var pestReport = new PestReport(command);
            var savedReport = pestReportRepository.save(pestReport);

            return Result.success(savedReport.getId());
        } catch (Exception e) {
            return Result.failure(ApplicationError.unexpected("record-pest-report", e.getMessage()));
        }
    }

    @Override
    public Result<Long, ApplicationError> handle(MarkPestReportAsInspectedCommand command) {
        try {
            var result = pestReportRepository.findById(command.pestReportId());

            if (result.isEmpty()) {
                return Result.failure(ApplicationError.notFound("PestReport", command.pestReportId().toString()));
            }

            var pestReport = result.get();

            try {
                pestReport.markInspected();
            } catch (InvalidPestReportStatusTransitionException | IllegalStateException e) {
                return Result.failure(ApplicationError.conflict("PestReport", e.getMessage()));
            }

            var savedReport = pestReportRepository.save(pestReport);

            return Result.success(savedReport.getId());
        } catch (Exception e) {
            return Result.failure(ApplicationError.unexpected("mark-pest-report-as-inspected", e.getMessage()));
        }
    }

    @Override
    public Result<Long, ApplicationError> handle(ResolvePestReportCommand command) {
        try {
            var result = pestReportRepository.findById(command.pestReportId());

            if (result.isEmpty()) {
                return Result.failure(ApplicationError.notFound("PestReport", command.pestReportId().toString()));
            }

            var pestReport = result.get();

            try {
                pestReport.resolve();
            } catch (InvalidPestReportStatusTransitionException | IllegalStateException e) {
                return Result.failure(ApplicationError.conflict("PestReport", e.getMessage()));
            }

            var savedReport = pestReportRepository.save(pestReport);

            return Result.success(savedReport.getId());
        } catch (Exception e) {
            return Result.failure(
                    ApplicationError.unexpected("resolve-pest-report", e.getMessage()));
        }
    }

    @Override
    public Result<Long, ApplicationError> handle(DeletePestReportCommand command) {
        try {
            var result = pestReportRepository.findById(
                    command.pestReportId()
            );

            if (result.isEmpty()) {
                return Result.failure(ApplicationError.notFound("PestReport", command.pestReportId().toString()));
            }

            pestReportRepository.deleteById(command.pestReportId());

            return Result.success(command.pestReportId());
        } catch (Exception e) {
            return Result.failure(ApplicationError.unexpected("delete-pest-report", e.getMessage()));
        }
    }
}