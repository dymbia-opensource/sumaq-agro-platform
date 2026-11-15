package com.dymbia.sumaqagro.platform.crop.interfaces.rest.resources;

/**
 * API request for publishing a regional bulletin.
 */
public record PublishRegionalBulletinResource(
        Long agronomistId,
        String region,
        String title,
        String content
) {
}
