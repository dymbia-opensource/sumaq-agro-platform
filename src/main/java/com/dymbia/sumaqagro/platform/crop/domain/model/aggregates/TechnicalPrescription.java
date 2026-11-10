package com.dymbia.sumaqagro.platform.crop.domain.model.aggregates;

import com.dymbia.sumaqagro.platform.crop.domain.model.commands.IssueTechnicalPrescriptionCommand;
import com.dymbia.sumaqagro.platform.crop.domain.model.valueobjects.TechnicalPrescriptionStatus;
import com.dymbia.sumaqagro.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Aggregate root representing a technical prescription for a pest report.
 */
@Getter
public class TechnicalPrescription extends AbstractDomainAggregateRoot<TechnicalPrescription> {

    private Long id;
    private final Long pestReportId;
    private final Long plotId;
    private final Long agronomistId;
    private final String product;
    private final String dosage;
    private final String instructions;
    private final LocalDate applicationDate;
    private TechnicalPrescriptionStatus status;
    private Instant appliedAt;

    /**
     * Creates a new technical prescription with ISSUED status.
     *
     * @param command validated data required to issue the prescription
     * @param plotId identifier of the plot associated with the pest report
     */
    private TechnicalPrescription(IssueTechnicalPrescriptionCommand command, Long plotId) {
        Objects.requireNonNull(command, "Command is required");

        if (plotId == null || plotId <= 0) {
            throw new IllegalArgumentException("Plot ID must be greater than zero");
        }

        this.pestReportId = command.pestReportId();
        this.plotId = plotId;
        this.agronomistId = command.agronomistId();
        this.product = command.product();
        this.dosage = command.dosage();
        this.instructions = command.instructions();
        this.applicationDate = command.applicationDate();
        this.status = TechnicalPrescriptionStatus.ISSUED;
    }

    /**
     * Creates a new technical prescription with ISSUED status and validates that the application date is not in the past.
     *
     * @param command validated data required to issue the prescription
     * @param plotId identifier of the plot associated with the pest report
     * @param today current date for validation
     */
    public TechnicalPrescription(IssueTechnicalPrescriptionCommand command, Long plotId, LocalDate today) {
        this(command, plotId);

        Objects.requireNonNull(today, "Current date is required");

        if (applicationDate.isBefore(today)) {
            throw new IllegalArgumentException(
                    "Application date cannot be before today"
            );
        }
    }

    /**
     * Reconstitutes an existing technical prescription from persistence state.
     *
     * @param id prescription identifier
     * @param pestReportId associated pest report identifier
     * @param plotId associated plot identifier
     * @param agronomistId agronomist identifier
     * @param product prescribed product
     * @param dosage prescribed dosage
     * @param instructions application instructions
     * @param applicationDate scheduled application date
     * @param status current prescription status
     * @param appliedAt timestamp of when the prescription was applied (if applicable)
     * @return the reconstituted prescription
     */
    public static TechnicalPrescription reconstitute(
            Long id,
            Long pestReportId,
            Long plotId,
            Long agronomistId,
            String product,
            String dosage,
            String instructions,
            LocalDate applicationDate,
            TechnicalPrescriptionStatus status,
            Instant appliedAt
    )
    {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Technical prescription ID must be greater than zero");
        }

        Objects.requireNonNull(status, "Prescription status is required");

        if (status == TechnicalPrescriptionStatus.APPLIED && appliedAt == null) {
            throw new IllegalArgumentException("An applied prescription requires a confirmation timestamp");
        }

        if (status == TechnicalPrescriptionStatus.ISSUED && appliedAt != null) {
            throw new IllegalArgumentException("An issued prescription cannot have a confirmation timestamp");
        }

        var command = new IssueTechnicalPrescriptionCommand(
                pestReportId,
                agronomistId,
                product,
                dosage,
                instructions,
                applicationDate
        );

        var prescription = new TechnicalPrescription(command, plotId);
        prescription.id = id;
        prescription.status = status;
        prescription.appliedAt = appliedAt;

        return prescription;
    }

    /**
     * Registers the application of the technical prescription, updating its status to APPLIED.
     *
     * @param confirmationTime timestamp of when the prescription was applied
     */
    public void registerApplication(Instant confirmationTime) {
        if (status != TechnicalPrescriptionStatus.ISSUED) {
            throw new IllegalStateException("The technical prescription has already been applied");
        }

        this.appliedAt = Objects.requireNonNull(confirmationTime, "Confirmation timestamp is required");
        this.status = TechnicalPrescriptionStatus.APPLIED;
    }
}