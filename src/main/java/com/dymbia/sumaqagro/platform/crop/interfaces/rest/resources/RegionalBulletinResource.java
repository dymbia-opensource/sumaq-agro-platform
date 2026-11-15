package com.dymbia.sumaqagro.platform.crop.interfaces.rest.resources;

import java.time.Instant;

/**
 * API response containing a published regional bulletin.
 */
public record RegionalBulletinResource(
        Long id,
        Long agronomistId,
        String region,
        String title,
        String content,
        Instant publishedAt
) {
}
