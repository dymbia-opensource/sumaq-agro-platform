package com.dymbia.sumaqagro.platform.crop.domain.model.events;

import com.dymbia.sumaqagro.platform.crop.domain.model.valueobjects.AgroclimaticAlertSeverity;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.time.Instant;

/**
 * Carries the details of a newly issued agroclimatic alert.
 */
@Getter
public class AgroclimaticAlertIssuedEvent extends ApplicationEvent {

    private final String title;
    private final String description;
    private final AgroclimaticAlertSeverity severity;
    private final String region;
    private final Instant issuedAt;
    private final String alertSource;

    /**
     * Constructs a new AgroclimaticAlertIssuedEvent.
     *
     * @param source      the object that published the event
     * @param title       the title of the alert
     * @param description the description of the alert
     * @param severity    the severity level of the alert
     * @param region      the region affected by the alert
     * @param issuedAt    the timestamp when the alert was issued
     * @param alertSource the source of the alert information
     */
    public AgroclimaticAlertIssuedEvent(
            Object source,
            String title,
            String description,
            AgroclimaticAlertSeverity severity,
            String region,
            Instant issuedAt,
            String alertSource
    ) {
        super(source);
        this.title = title;
        this.description = description;
        this.severity = severity;
        this.region = region;
        this.issuedAt = issuedAt;
        this.alertSource = alertSource;
    }
}
