package com.dymbia.sumaqagro.platform.crop.domain.model.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.time.Instant;

/**
 * Domain event representing the completion of a field inspection.
 */
@Getter
public class FieldInspectionCompletedEvent extends ApplicationEvent {

    private final Long fieldInspectionId;
    private final Long pestReportId;
    private final Long plotId;
    private final Instant completedAt;

    /**
     * Creates an event describing a completed field inspection.
     *
     * @param source the object where the event originated
     * @param fieldInspectionId completed inspection identifier
     * @param pestReportId associated pest report identifier
     * @param plotId inspected plot identifier
     * @param completedAt actual completion date and time
     */
    public FieldInspectionCompletedEvent(Object source, Long fieldInspectionId, Long pestReportId, Long plotId, Instant completedAt) {
        super(source);
        this.fieldInspectionId = fieldInspectionId;
        this.pestReportId = pestReportId;
        this.plotId = plotId;
        this.completedAt = completedAt;
    }
}