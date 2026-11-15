package com.dymbia.sumaqagro.platform.farming.domain.model.commands;

import com.dymbia.sumaqagro.platform.farming.domain.model.valueobjects.CropType;
import com.dymbia.sumaqagro.platform.farming.domain.model.valueobjects.SeedVariety;

import java.time.LocalDate;

/**
 *
 * @param plotId            ID of the plot where the campaign
 * @param season            season in the format {@code YYYY-I} or {@code YYYY-II}
 * @param cropType          Type of crop in the campaign
 * @param seedVariety       Variety of seed to be used
 * @param customSeedVariety Flag indicating if a custom seed variety is being used
 * @param sowingDate        sowing date, optional
 */
public record StartCropCampaignCommand(Long plotId, String season, CropType cropType, SeedVariety seedVariety, boolean customSeedVariety, LocalDate sowingDate) {
    /**
     * Validates the command parameters.
     *
     * @throws IllegalArgumentException when the plotId, season
     */
    public StartCropCampaignCommand{
        if(plotId == null||plotId <= 0) {
            throw new IllegalArgumentException("crop.campaign.error.plotId.invalid");
        }
        if(season == null || !season.matches("^\\d{4}-(I|II)$")) {
            throw new IllegalArgumentException("crop.campaign.error.season.invalid");
        }
        if(cropType == null) {
            throw new IllegalArgumentException("crop.campaign.error.cropType.required");
        }
        if(seedVariety == null) {
            throw new IllegalArgumentException("crop.campaign.error.seedVariety.notBlank");
        }
    }

}
