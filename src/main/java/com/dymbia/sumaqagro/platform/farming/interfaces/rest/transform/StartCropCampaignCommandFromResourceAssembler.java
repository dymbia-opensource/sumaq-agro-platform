package com.dymbia.sumaqagro.platform.farming.interfaces.rest.transform;

import com.dymbia.sumaqagro.platform.farming.domain.model.commands.StartCropCampaignCommand;
import com.dymbia.sumaqagro.platform.farming.domain.model.valueobjects.SeedVariety;
import com.dymbia.sumaqagro.platform.farming.interfaces.rest.resources.StartCropCampaignResource;

import java.util.Objects;

/**
 * Converts the start crop campaign request body into its command.
 */
public final class StartCropCampaignCommandFromResourceAssembler {

    private StartCropCampaignCommandFromResourceAssembler() {
    }

    /**
     * Builds the start crop campaign command.
     *
     * @param resource the request body
     * @return the command
     */
    public static StartCropCampaignCommand toCommandFromResource(StartCropCampaignResource resource) {
        Objects.requireNonNull(resource, "Start crop campaign request body is required.");
        return new StartCropCampaignCommand(
                resource.fieldPlotId(),
                resource.season(),
                CropTypeFromResourceAssembler.toCropTypeFromResource(resource.cropType()),
                new SeedVariety(resource.seedVariety()),
                resource.customSeedVariety(),
                resource.sowingDate());
    }
}
