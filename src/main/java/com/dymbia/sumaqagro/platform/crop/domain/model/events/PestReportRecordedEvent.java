package com.dymbia.sumaqagro.platform.crop.domain.model.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * Domain event representing the recording of a pest report.
 *
 * <p>
 * This event is published when a pest report is successfully recorded for a specific plot.
 * </p>
 */
@Getter
public class PestReportRecordedEvent extends ApplicationEvent {

    /**
     * The ID of the recorded pest report.
     */
    private final Long pestReportId;

    /**
     * The ID of the plot where the pest report was recorded.
     */
    private final Long plotId;
    private final Long reporterId;

    /**
     * Constructor for PestReportRecordedEvent.
     *
     * @param source        The object that published the event.
     * @param pestReportId  The ID of the recorded pest report.
     * @param plotId        The ID of the plot where the pest report was recorded.
     * @param reporterId    The ID of the user who reported the pest.
     */
    public PestReportRecordedEvent(Object source, Long pestReportId, Long plotId, Long reporterId) {
        super(source);
        this.pestReportId = pestReportId;
        this.plotId = plotId;
        this.reporterId = reporterId;
    }
}
