package com.dymbia.sumaqagro.platform.crop.domain.model.commands;

/**
 * Requests the publication of a bulletin for a region.
 */
public record PublishRegionalBulletinCommand(
        Long agronomistId,
        String region,
        String title,
        String content
) {

    public PublishRegionalBulletinCommand {
        if (agronomistId == null || agronomistId <= 0) {
            throw new IllegalArgumentException(
                    "Agronomist ID must be greater than zero"
            );
        }

        region = requiredText(region, "Region");
        title = requiredText(title, "Bulletin title");
        content = requiredText(content, "Bulletin content");
    }

    private static String requiredText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " is required");
        }

        return value.strip();
    }
}