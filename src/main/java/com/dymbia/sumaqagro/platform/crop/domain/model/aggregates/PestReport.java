package com.dymbia.sumaqagro.platform.crop.domain.model.aggregates;

import com.dymbia.sumaqagro.platform.crop.domain.model.commands.RecordPestReportCommand;
import com.dymbia.sumaqagro.platform.crop.domain.model.valueobjects.PestReportSeverity;
import com.dymbia.sumaqagro.platform.crop.domain.model.valueobjects.PestReportStatus;
import com.dymbia.sumaqagro.platform.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;
import lombok.Getter;

/**
 * Represents a pest report submitted by a user for a specific plot.
 */

@Getter
@Entity
public class PestReport extends AuditableAbstractAggregateRoot<PestReport> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long plotId;

    @Column(nullable = false)
    private Long reporterId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PestReportSeverity severity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PestReportStatus status;

    /**
     * Required by JPA. It's obligatory. It must be empty and protected. It is used by JPA to create an instance of the entity when retrieving it from the database.
     */
    protected PestReport() {

    }

    /**
     * Creates a pest report from a validated record command.
     *
     * @param command command containing the plot, reporter, description and severity
     */
    public PestReport(RecordPestReportCommand command) {
        this.plotId = command.plotId();
        this.reporterId = command.reporterId();
        this.description = command.description().trim();
        this.severity = command.severity();
        this.status = PestReportStatus.PENDING;
    }


    public void markInspected() {
        if (status != PestReportStatus.PENDING) {
            throw new IllegalStateException(
                    "Only pending reports can be marked as inspected"
            );
        }

        status = PestReportStatus.INSPECTED;
    }

    public void resolve() {
        if (status != PestReportStatus.INSPECTED) {
            throw new IllegalStateException(
                    "Only inspected reports can be resolved"
            );
        }

        status = PestReportStatus.RESOLVED;
    }
}