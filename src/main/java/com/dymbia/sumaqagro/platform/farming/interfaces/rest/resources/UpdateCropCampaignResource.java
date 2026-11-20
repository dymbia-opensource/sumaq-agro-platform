package com.dymbia.sumaqagro.platform.farming.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

/**
 * Request body to change some data of a crop campaign. Only the fields that are sent change.
 *
 * @param cropType          the crop type ({@code ANDEAN_POTATO} or {@code SPECIALTY_COFFEE}), optional
 * @param seedVariety       the seed variety, optional
 * @param customSeedVariety whether the seed variety is not in the catalog, used with the seed variety
 * @param sowingDate        the sowing date in {@code YYYY-MM-DD} format, optional
 */
@Schema(name = "UpdateCropCampaignRequest",
        description = "Request payload to change a crop campaign in progress. Send at least one field.")
public record UpdateCropCampaignResource(
        @Schema(description = "Crop type, optional", example = "SPECIALTY_COFFEE", nullable = true,
                allowableValues = {"ANDEAN_POTATO", "SPECIALTY_COFFEE"})
        String cropType,
        @Schema(description = "Seed variety, optional", example = "Caturra", maxLength = 60, nullable = true)
        String seedVariety,
        @Schema(description = "Whether the seed variety is not in the catalog, used with the seed variety",
                example = "false", nullable = true)
        Boolean customSeedVariety,
        @Schema(description = "Sowing date, optional", example = "2026-10-20", nullable = true)
        LocalDate sowingDate) {
}
