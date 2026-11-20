package com.dymbia.sumaqagro.platform.farming.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

/**
 * Response body of a crop campaign.
 *
 * @param id                the campaign id
 * @param fieldPlotId       the plot where the campaign takes place
 * @param season            the season
 * @param cropType          the crop type
 * @param seedVariety       the seed variety
 * @param customSeedVariety whether the variety is not in the catalog
 * @param sowingDate        the sowing date, or null if the crop is not sown yet
 * @param status            the status ({@code IN_PROGRESS} or {@code FINISHED})
 */
@Schema(name = "CropCampaignResponse", description = "Crop campaign of a field plot")
public record CropCampaignResource(
        @Schema(description = "Crop campaign identifier", example = "1")
        Long id,
        @Schema(description = "Field plot where the campaign takes place", example = "1")
        Long fieldPlotId,
        @Schema(description = "Season of the campaign", example = "2026-I")
        String season,
        @Schema(description = "Crop type", example = "ANDEAN_POTATO", allowableValues = {"ANDEAN_POTATO", "SPECIALTY_COFFEE"})
        String cropType,
        @Schema(description = "Seed variety", example = "Yungay")
        String seedVariety,
        @Schema(description = "Whether the seed variety is not in the catalog", example = "false")
        boolean customSeedVariety,
        @Schema(description = "Sowing date, null if the crop is not sown yet", example = "2026-10-15")
        LocalDate sowingDate,
        @Schema(description = "Campaign status", example = "IN_PROGRESS", allowableValues = {"IN_PROGRESS", "FINISHED"})
        String status) {
}
