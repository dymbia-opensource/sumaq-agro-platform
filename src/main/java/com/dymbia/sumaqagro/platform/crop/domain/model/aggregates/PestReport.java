package com.dymbia.sumaqagro.platform.crop.domain.model.aggregates;

import com.dymbia.sumaqagro.platform.crop.domain.model.valueobjects.PestReportSeverity;
import com.dymbia.sumaqagro.platform.crop.domain.model.valueobjects.PestReportStatus;
import com.dymbia.sumaqagro.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;

public class PestReport extends AbstractDomainAggregateRoot<PestReport> {

    private Long id;
    private Long plotId;
    private Long reporterId;
    private String description;
    private PestReportSeverity severity;
    private PestReportStatus status;

    /**
     * Default constructor for PestReport.
     * Initializes the status to PENDING.
     */
    public PestReport() {

    }

    /**
     * Constructor for PestReport with all required fields.
     *
     * @param plotId      The ID of the plot where the incident occurred.
     * @param reporterId  The ID of the reporter.
     * @param description The description of the incident.
     * @param severity    The severity of the incident.
     */
    public PestReport(Long plotId, Long reporterId, String description, PestReportSeverity severity) {
        this.plotId = plotId;
        this.reporterId = reporterId;
        this.description = description;
        this.severity = severity;
        this.status = PestReportStatus.PENDING; // Default status
    }

}
