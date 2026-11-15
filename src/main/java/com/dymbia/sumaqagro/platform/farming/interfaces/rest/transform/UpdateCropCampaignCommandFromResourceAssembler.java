package com.dymbia.sumaqagro.platform.farming.interfaces.rest.transform;

import com.dymbia.sumaqagro.platform.farming.domain.model.commands.UpdateCropCampaignCommand;
import com.dymbia.sumaqagro.platform.farming.domain.model.valueobjects.SeedVariety;
import com.dymbia.sumaqagro.platform.farming.interfaces.rest.resources.UpdateCropCampaignResource;

import java.util.Objects;

/**
 * Converts the partial update request body of a crop campaign into its command.
 */
public final class UpdateCropCampaignCommandFromResourceAssembler {

    private UpdateCropCampaignCommandFromResourceAssembler() {
    }

    /**
     * Builds the update crop campaign command. A field that is not sent stays null and does not change.
     *
     * @param cropCampaignId the campaign id from the path
     * @param resource       the request body
     * @return the command
     */
    public static UpdateCropCampaignCommand toCommandFromResource(Long cropCampaignId,
                                                                  UpdateCropCampaignResource resource) {
        Objects.requireNonNull(resource, "Update crop campaign request body is required.");
        var cropType = resource.cropType() == null ? null
                : CropTypeFromResourceAssembler.toCropTypeFromResource(resource.cropType());
        var seedVariety = resource.seedVariety() == null ? null : new SeedVariety(resource.seedVariety());
        return new UpdateCropCampaignCommand(cropCampaignId, cropType, seedVariety,
                Boolean.TRUE.equals(resource.customSeedVariety()), resource.sowingDate());
    }
}
