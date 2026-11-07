package com.dymbia.sumaqagro.platform.crop.domain.model.aggregates;

import com.dymbia.sumaqagro.platform.crop.domain.model.commands.RecordPestReportCommand;
import com.dymbia.sumaqagro.platform.crop.domain.model.valueobjects.PestReportSeverity;
import com.dymbia.sumaqagro.platform.crop.domain.model.valueobjects.PestReportStatus;
import com.dymbia.sumaqagro.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;

import java.util.Objects;

/**
 * Aggregate root representing a pest report for a specific plot.
 */
@Getter
public class PestReport extends AbstractDomainAggregateRoot<PestReport> {

    private Long id;
    private Long plotId;
    private Long reporterId;
    private String description;
    private PestReportSeverity severity;
    private PestReportStatus status;

    /**
     * Creates a new pest report with PENDING status.
     *
     * @param command validated data required to record the report
     */
    public PestReport(RecordPestReportCommand command) {
        Objects.requireNonNull(command, "Command is required");

        this.plotId = command.plotId();
        this.reporterId = command.reporterId();
        this.description = command.description().trim();
        this.severity = command.severity();
        this.status = PestReportStatus.PENDING;
    }

    /**
     * Reconstitutes an existing pest report from persistence state.
     *
     * @param id report identifier
     * @param plotId plot identifier
     * @param reporterId reporter identifier
     * @param description report description
     * @param severity report severity
     * @param status current report status
     * @return the reconstituted report
     */
    public static PestReport reconstitute(
            Long id,
            Long plotId,
            Long reporterId,
            String description,
            PestReportSeverity severity,
            PestReportStatus status
    ) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException(
                    "Pest report ID must be greater than zero"
            );
        }

        Objects.requireNonNull(status, "Pest report status is required");

        var command = new RecordPestReportCommand(
                plotId,
                reporterId,
                description,
                severity
        );

        var report = new PestReport(command);
        report.id = id;
        report.status = status;

        return report;
    }

    /**
     * Marks a pending report as inspected.
     *
     * @throws IllegalStateException if the report is not pending
     */
    public void markInspected() {
        if (this.status != PestReportStatus.PENDING) {
            throw new IllegalStateException(
                    "Only pending pest reports can be marked as inspected"
            );
        }

        this.status = PestReportStatus.INSPECTED;
    }

    /**
     * Resolves an inspected report.
     *
     * @throws IllegalStateException if the report is not inspected
     */
    public void resolve() {
        if (this.status != PestReportStatus.INSPECTED) {
            throw new IllegalStateException(
                    "Only inspected pest reports can be resolved"
            );
        }

        this.status = PestReportStatus.RESOLVED;
    }
}