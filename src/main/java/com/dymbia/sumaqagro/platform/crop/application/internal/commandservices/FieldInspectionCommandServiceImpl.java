package com.dymbia.sumaqagro.platform.crop.application.internal.commandservices;

import com.dymbia.sumaqagro.platform.crop.application.commandservices.FieldInspectionCommandService;
import com.dymbia.sumaqagro.platform.crop.domain.exceptions.FieldInspectionAlreadyCompletedException;
import com.dymbia.sumaqagro.platform.crop.domain.model.aggregates.FieldInspection;
import com.dymbia.sumaqagro.platform.crop.domain.model.commands.CompleteFieldInspectionCommand;
import com.dymbia.sumaqagro.platform.crop.domain.model.commands.ScheduleFieldInspectionCommand;
import com.dymbia.sumaqagro.platform.crop.domain.model.valueobjects.PestReportStatus;
import com.dymbia.sumaqagro.platform.crop.domain.repositories.FieldInspectionRepository;
import com.dymbia.sumaqagro.platform.crop.domain.repositories.PestReportRepository;
import com.dymbia.sumaqagro.platform.shared.application.result.ApplicationError;
import com.dymbia.sumaqagro.platform.shared.application.result.Result;
import org.springframework.stereotype.Service;

import java.time.Instant;

/**
 * Application service that executes field inspection commands.
 */
@Service
public class FieldInspectionCommandServiceImpl implements FieldInspectionCommandService {

    private final FieldInspectionRepository inspectionRepository;
    private final PestReportRepository pestReportRepository;

    public FieldInspectionCommandServiceImpl(FieldInspectionRepository inspectionRepository, PestReportRepository pestReportRepository) {
        this.inspectionRepository = inspectionRepository;
        this.pestReportRepository = pestReportRepository;
    }

    @Override
    public Result<Long, ApplicationError> handle(ScheduleFieldInspectionCommand command) {
        try {
            var reportResult = pestReportRepository.findById(
                    command.pestReportId()
            );

            if (reportResult.isEmpty()) {
                return Result.failure(
                        ApplicationError.notFound(
                                "PestReport",
                                command.pestReportId().toString()
                        )
                );
            }

            var pestReport = reportResult.get();

            if (pestReport.getStatus() != PestReportStatus.PENDING) {
                return Result.failure(
                        ApplicationError.conflict(
                                "PestReport",
                                "Only pending pest reports can be scheduled for inspection"
                        )
                );
            }

            if (inspectionRepository.existsByPestReportId(
                    command.pestReportId()
            )) {
                return Result.failure(
                        ApplicationError.conflict(
                                "FieldInspection",
                                "The pest report already has an inspection"
                        )
                );
            }

            FieldInspection inspection;

            try {
                inspection = new FieldInspection(
                        command,
                        pestReport.getPlotId(),
                        Instant.now()
                );
            } catch (IllegalArgumentException e) {
                return Result.failure(
                        ApplicationError.validationError(
                                "FieldInspection",
                                e.getMessage()
                        )
                );
            }

            var savedInspection = inspectionRepository.save(inspection);

            return Result.success(savedInspection.getId());
        } catch (Exception e) {
            return Result.failure(
                    ApplicationError.unexpected(
                            "schedule-field-inspection",
                            e.getMessage()
                    )
            );
        }
    }

    @Override
    public Result<Long, ApplicationError> handle(CompleteFieldInspectionCommand command) {
        try {
            var result = inspectionRepository.findById(
                    command.fieldInspectionId()
            );

            if (result.isEmpty()) {
                return Result.failure(
                        ApplicationError.notFound(
                                "FieldInspection",
                                command.fieldInspectionId().toString()
                        )
                );
            }

            var inspection = result.get();

            try {
                inspection.complete(
                        command.findings(),
                        Instant.now()
                );
            } catch (FieldInspectionAlreadyCompletedException
                     | IllegalStateException e) {
                return Result.failure(
                        ApplicationError.conflict(
                                "FieldInspection",
                                e.getMessage()
                        )
                );
            } catch (IllegalArgumentException e) {
                return Result.failure(
                        ApplicationError.validationError(
                                "FieldInspection",
                                e.getMessage()
                        )
                );
            }

            var savedInspection = inspectionRepository.save(inspection);

            return Result.success(savedInspection.getId());
        } catch (Exception e) {
            return Result.failure(
                    ApplicationError.unexpected(
                            "complete-field-inspection",
                            e.getMessage()
                    )
            );
        }
    }
}