package com.dymbia.sumaqagro.platform.crop.domain.model.commands;

import com.dymbia.sumaqagro.platform.crop.domain.model.valueobjects.AgroclimaticAlertSeverity;

import java.time.Instant;

/**
 * Requests the issuance of an agroclimatic alert for a region.
 */
public record IssueAgroclimaticAlertCommand(
        String title,
        String description,
        AgroclimaticAlertSeverity severity,
        String region,
        Instant issuedAt,
        String source
) {

    public IssueAgroclimaticAlertCommand {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Alert title is required");
        }

        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Alert description is required");
        }

        if (severity == null) {
            throw new IllegalArgumentException("Alert severity is required");
        }

        if (region == null || region.isBlank()) {
            throw new IllegalArgumentException("Region is required");
        }

        if (issuedAt == null) {
            throw new IllegalArgumentException("Alert issuance time is required");
        }

        if (source == null || source.isBlank()) {
            throw new IllegalArgumentException("Alert source is required");
        }

        title = title.strip();
        description = description.strip();
        region = region.strip();
        source = source.strip();
    }
}
