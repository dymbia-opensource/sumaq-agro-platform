package com.dymbia.sumaqagro.platform.crop.domain.model.aggregates;

import com.dymbia.sumaqagro.platform.crop.domain.model.commands.IssueAgroclimaticAlertCommand;
import com.dymbia.sumaqagro.platform.crop.domain.model.events.AgroclimaticAlertIssuedEvent;
import com.dymbia.sumaqagro.platform.crop.domain.model.valueobjects.AgroclimaticAlertSeverity;
import com.dymbia.sumaqagro.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;

import java.time.Instant;
import java.util.Objects;

/**
 * Aggregate root representing an agroclimatic alert issued for a region.
 */
@Getter
public class AgroclimaticAlert extends AbstractDomainAggregateRoot<AgroclimaticAlert> {

    private final Long id;
    private final String title;
    private final String description;
    private final AgroclimaticAlertSeverity severity;
    private final String region;
    private final Instant issuedAt;
    private final String source;

    private AgroclimaticAlert(Long id, IssueAgroclimaticAlertCommand command) {
        Objects.requireNonNull(command, "Command is required");

        this.id = id;
        this.title = command.title();
        this.description = command.description();
        this.severity = command.severity();
        this.region = command.region();
        this.issuedAt = command.issuedAt();
        this.source = command.source();
    }

    /**
     * Issues an alert and registers its event for publication after saving.
     */
    public AgroclimaticAlert(IssueAgroclimaticAlertCommand command) {
        this(null, command);

        registerDomainEvent(new AgroclimaticAlertIssuedEvent(
                this, this.title, this.description, this.severity,
                this.region, this.issuedAt, this.source
        ));
    }

    /**
     * Restores a stored alert without registering a new issuance event.
     */
    public static AgroclimaticAlert reconstitute(
            Long id,
            String title,
            String description,
            AgroclimaticAlertSeverity severity,
            String region,
            Instant issuedAt,
            String source
    ) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Agroclimatic alert ID must be greater than zero");
        }

        var command = new IssueAgroclimaticAlertCommand(
                title, description, severity, region, issuedAt, source
        );

        return new AgroclimaticAlert(id, command);
    }
}
