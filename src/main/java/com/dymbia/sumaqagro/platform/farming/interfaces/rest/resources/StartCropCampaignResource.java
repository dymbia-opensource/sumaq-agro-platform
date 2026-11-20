package com.dymbia.sumaqagro.platform.farming.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

/**
 * Request body to start a crop campaign.
 *
 * @param fieldPlotId       the plot where the campaign takes place
 * @param season            the season, {@code YYYY-I} or {@code YYYY-II}
 * @param cropType          the crop type ({@code ANDEAN_POTATO} or {@code SPECIALTY_COFFEE})
 * @param seedVariety       the seed variety
 * @param customSeedVariety whether the producer typed a variety that is not in the catalog
 * @param sowingDate        the sowing date, optional
 */
@Schema(name = "StartCropCampaignRequest", description = "Request payload to start a crop campaign")
public record StartCropCampaignResource(
        @Schema(description = "Field plot where the campaign takes place", example = "1")
        Long fieldPlotId,
        @Schema(description = "Season, YYYY-I or YYYY-II", example = "2026-I", pattern = "^\\d{4}-(I|II)$")
        String season,
        @Schema(description = "Crop type", example = "ANDEAN_POTATO", allowableValues = {"ANDEAN_POTATO", "SPECIALTY_COFFEE"})
        String cropType,
        @Schema(description = "Seed variety", example = "Yungay", maxLength = 60)
        String seedVariety,
        @Schema(description = "Whether the producer typed a variety that is not in the catalog", example = "false")
        boolean customSeedVariety,
        @Schema(description = "Sowing date, optional", example = "2026-10-15", nullable = true)
        LocalDate sowingDate) {
}
