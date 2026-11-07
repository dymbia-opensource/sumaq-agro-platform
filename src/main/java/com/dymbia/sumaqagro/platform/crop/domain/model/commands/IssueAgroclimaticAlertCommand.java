package com.dymbia.sumaqagro.platform.crop.domain.model.commands;

import com.dymbia.sumaqagro.platform.crop.domain.model.valueobjects.AgroclimaticAlertSeverity;

import java.time.Instant;

/**
 * Command to issue an agroclimatic alert for a specific region.
 */
public record IssueAgroclimaticAlertCommand(
        String title,
        String description,
        AgroclimaticAlertSeverity severity,
        String region,
        Instant issuedAt,
        String source
) {
}