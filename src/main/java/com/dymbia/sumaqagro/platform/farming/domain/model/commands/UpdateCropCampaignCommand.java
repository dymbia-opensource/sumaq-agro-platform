package com.dymbia.sumaqagro.platform.farming.domain.model.commands;

import com.dymbia.sumaqagro.platform.farming.domain.model.valueobjects.CropType;
import com.dymbia.sumaqagro.platform.farming.domain.model.valueobjects.SeedVariety;

import java.time.LocalDate;

/**
 * Command to change some data of a crop campaign in progress.
 * <p>
 * Only the values that are not null change. They are the steps of the EventStorming
 * (select crop type, specify seed variety and record sowing date) sent together.
 * </p>
 *
 * @param cropCampaignId    the campaign id
 * @param cropType          the new crop type, or null to keep it
 * @param seedVariety       the new seed variety, or null to keep it
 * @param customSeedVariety whether the new seed variety is not in the catalog; used only with a seed variety
 * @param sowingDate        the sowing date, or null to keep it
 * @since 1.0
 */
public record UpdateCropCampaignCommand(Long cropCampaignId, CropType cropType, SeedVariety seedVariety,
                                        boolean customSeedVariety, LocalDate sowingDate) {
    /**
     * Validates the command parameters.
     *
     * @throws IllegalArgumentException when the id is invalid or there is nothing to change
     */
    public UpdateCropCampaignCommand {
        if (cropCampaignId == null || cropCampaignId <= 0) {
            throw new IllegalArgumentException("crop.campaign.error.id.invalid");
        }
        if (cropType == null && seedVariety == null && sowingDate == null) {
            throw new IllegalArgumentException("crop.campaign.error.update.empty");
        }
    }
}
