package com.dymbia.sumaqagro.platform.crop.domain.model.aggregates;

import com.dymbia.sumaqagro.platform.crop.domain.model.commands.ScheduleFieldInspectionCommand;
import com.dymbia.sumaqagro.platform.crop.domain.model.events.FieldInspectionCompletedEvent;
import com.dymbia.sumaqagro.platform.crop.domain.model.valueobjects.FieldInspectionStatus;
import com.dymbia.sumaqagro.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;

import java.time.Instant;
import java.util.Objects;

/**
 * Aggregate root representing a field inspection for a pest report.
 */
@Getter
public class FieldInspection extends AbstractDomainAggregateRoot<FieldInspection> {

    private Long id;
    private Long pestReportId;
    private Long plotId;
    private Long agronomistId;
    private Instant scheduledAt;
    private FieldInspectionStatus status;
    private String findings;
    private Instant completedAt;

    /**
     * Initializes the common inspection data.
     * Used internally for creation and reconstitution.
     */
    private FieldInspection(ScheduleFieldInspectionCommand command, Long plotId) {
        Objects.requireNonNull(command, "Command is required");

        if (plotId == null || plotId <= 0) {
            throw new IllegalArgumentException("Plot ID must be greater than zero");
        }

        this.pestReportId = command.pestReportId();
        this.plotId = plotId;
        this.agronomistId = command.agronomistId();
        this.scheduledAt = command.scheduledAt();
        this.status = FieldInspectionStatus.SCHEDULED;
    }

    /**
     * Schedules a new inspection for a future date and time.
     */
    public FieldInspection(ScheduleFieldInspectionCommand command, Long plotId, Instant currentTime) {
        this(command, plotId);

        Objects.requireNonNull(currentTime, "Current time is required");

        if (!this.scheduledAt.isAfter(currentTime)) {
            throw new IllegalArgumentException("Inspection must be scheduled for a future date and time");
        }
    }

    /**
     * Reconstitutes a field inspection from persisted data.
     *
     * @param id            the unique identifier of the inspection
     * @param pestReportId  the associated pest report ID
     * @param plotId        the plot ID where the inspection is conducted
     * @param agronomistId  the agronomist ID assigned to the inspection
     * @param scheduledAt   the scheduled date and time for the inspection
     * @param status        the current status of the inspection
     * @param findings      the findings from the inspection (if completed)
     * @param completedAt   the date and time when the inspection was completed (if applicable)
     * @return a reconstituted FieldInspection aggregate
     */
    public static FieldInspection reconstitute(
            Long id,
            Long pestReportId,
            Long plotId,
            Long agronomistId,
            Instant scheduledAt,
            FieldInspectionStatus status,
            String findings,
            Instant completedAt
    ) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException(
                    "Field inspection ID must be greater than zero"
            );
        }

        Objects.requireNonNull(status, "Inspection status is required");

        var command = new ScheduleFieldInspectionCommand(
                pestReportId,
                agronomistId,
                scheduledAt
        );

        var inspection = new FieldInspection(command, plotId);

        if (status == FieldInspectionStatus.COMPLETED) {
            if (findings == null || findings.trim().length() < 10) {
                throw new IllegalArgumentException("Inspection findings must contain at least 10 characters");
            }

            Objects.requireNonNull(completedAt, "Completion date and time are required"
            );

            if (completedAt.isBefore(scheduledAt)) {
                throw new IllegalArgumentException("Completion time cannot precede the scheduled time");
            }
        } else if (findings != null || completedAt != null) {
            throw new IllegalArgumentException("Scheduled inspections cannot contain completion data");
        }

        inspection.id = id;
        inspection.status = status;
        inspection.findings = findings != null ? findings.trim() : null;
        inspection.completedAt = completedAt;

        return inspection;
    }

    /**
     * Marks the inspection as completed, recording the findings and completion time.
     *
     * @param findings    the findings from the inspection
     * @param currentTime the current date and time
     */
    public void complete(String findings, Instant currentTime) {
        if (this.status != FieldInspectionStatus.SCHEDULED) {
            throw new IllegalStateException("Only scheduled inspections can be completed");
        }

        if (this.id == null) {
            throw new IllegalStateException("Inspection must be saved before it can be completed");
        }

        Objects.requireNonNull(currentTime, "Current time is required");

        if (currentTime.isBefore(this.scheduledAt)) {
            throw new IllegalStateException("Inspection cannot be completed before its scheduled time");
        }

        if (findings == null || findings.trim().length() < 10) {
            throw new IllegalArgumentException("Inspection findings must contain at least 10 characters");
        }

        this.findings = findings.trim();
        this.completedAt = currentTime;
        this.status = FieldInspectionStatus.COMPLETED;

        registerDomainEvent(
                new FieldInspectionCompletedEvent(
                        this,
                        this.id,
                        this.pestReportId,
                        this.plotId,
                        this.completedAt
                )
        );
    }
}