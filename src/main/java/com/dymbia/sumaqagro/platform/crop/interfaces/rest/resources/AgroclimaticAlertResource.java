package com.dymbia.sumaqagro.platform.crop.interfaces.rest.resources;

import com.dymbia.sumaqagro.platform.crop.domain.model.valueobjects.AgroclimaticAlertSeverity;

import java.time.Instant;

/**
 * API response containing an agroclimatic alert.
 */
public record AgroclimaticAlertResource(
        Long id,
        String title,
        String description,
        AgroclimaticAlertSeverity severity,
        String region,
        Instant issuedAt,
        String source
) {
}
