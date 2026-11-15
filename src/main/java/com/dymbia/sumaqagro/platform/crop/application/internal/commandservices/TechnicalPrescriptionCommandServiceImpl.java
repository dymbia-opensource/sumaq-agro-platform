package com.dymbia.sumaqagro.platform.crop.application.internal.commandservices;

import com.dymbia.sumaqagro.platform.crop.application.commandservices.TechnicalPrescriptionCommandService;
import com.dymbia.sumaqagro.platform.crop.domain.exceptions.TechnicalPrescriptionAlreadyAppliedException;
import com.dymbia.sumaqagro.platform.crop.domain.model.aggregates.TechnicalPrescription;
import com.dymbia.sumaqagro.platform.crop.domain.model.commands.IssueTechnicalPrescriptionCommand;
import com.dymbia.sumaqagro.platform.crop.domain.model.commands.RegisterTechnicalPrescriptionApplicationCommand;
import com.dymbia.sumaqagro.platform.crop.domain.model.valueobjects.PestReportStatus;
import com.dymbia.sumaqagro.platform.crop.domain.repositories.PestReportRepository;
import com.dymbia.sumaqagro.platform.crop.domain.repositories.TechnicalPrescriptionRepository;
import com.dymbia.sumaqagro.platform.shared.application.result.ApplicationError;
import com.dymbia.sumaqagro.platform.shared.application.result.Result;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

/**
 * Executes technical prescription commands.
 */
@Service
public class TechnicalPrescriptionCommandServiceImpl implements TechnicalPrescriptionCommandService {

    private static final ZoneId BUSINESS_ZONE = ZoneId.of("America/Lima");

    private final TechnicalPrescriptionRepository prescriptionRepository;
    private final PestReportRepository pestReportRepository;

    public TechnicalPrescriptionCommandServiceImpl(TechnicalPrescriptionRepository prescriptionRepository, PestReportRepository pestReportRepository) {
        this.prescriptionRepository = prescriptionRepository;
        this.pestReportRepository = pestReportRepository;
    }

    @Override
    public Result<Long, ApplicationError> handle(IssueTechnicalPrescriptionCommand command) {
        var pestReportId = command.pestReportId();

        try {
            var reportResult = pestReportRepository.findById(pestReportId);

            if (reportResult.isEmpty()) {
                return Result.failure(ApplicationError.notFound(
                        "PestReport", pestReportId.toString()
                ));
            }

            var pestReport = reportResult.get();

            if (pestReport.getStatus() != PestReportStatus.INSPECTED) {
                return Result.failure(ApplicationError.conflict(
                        "PestReport",
                        "Only inspected pest reports can receive a prescription"
                ));
            }

            if (prescriptionRepository.existsByPestReportId(pestReportId)) {
                return Result.failure(ApplicationError.conflict(
                        "TechnicalPrescription",
                        "The pest report already has a technical prescription"
                ));
            }

            TechnicalPrescription prescription;

            try {
                prescription = new TechnicalPrescription(
                        command,
                        pestReport.getPlotId(),
                        LocalDate.now(BUSINESS_ZONE)
                );
            } catch (IllegalArgumentException e) {
                return Result.failure(ApplicationError.validationError(
                        "TechnicalPrescription", e.getMessage()
                ));
            }

            var savedPrescription = prescriptionRepository.save(prescription);
            return Result.success(savedPrescription.getId());
        } catch (Exception e) {
            return Result.failure(ApplicationError.unexpected(
                    "issue-technical-prescription", e.getMessage()
            ));
        }
    }

    @Override
    public Result<Long, ApplicationError> handle(RegisterTechnicalPrescriptionApplicationCommand command) {
        var prescriptionId = command.technicalPrescriptionId();

        try {
            var result = prescriptionRepository.findById(prescriptionId);

            if (result.isEmpty()) {
                return Result.failure(ApplicationError.notFound(
                        "TechnicalPrescription", prescriptionId.toString()
                ));
            }

            var prescription = result.get();
            prescription.registerApplication(Instant.now());

            var savedPrescription = prescriptionRepository.save(prescription);
            return Result.success(savedPrescription.getId());
        } catch (TechnicalPrescriptionAlreadyAppliedException e) {
            return Result.failure(ApplicationError.conflict(
                    "TechnicalPrescription", e.getMessage()
            ));
        } catch (Exception e) {
            return Result.failure(ApplicationError.unexpected(
                    "register-technical-prescription-application", e.getMessage()
            ));
        }
    }
}