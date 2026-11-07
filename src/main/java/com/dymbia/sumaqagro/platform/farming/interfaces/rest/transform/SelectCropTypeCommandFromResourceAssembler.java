package com.dymbia.sumaqagro.platform.farming.interfaces.rest.transform;

import com.dymbia.sumaqagro.platform.farming.domain.model.commands.SelectCropTypeCommand;
import com.dymbia.sumaqagro.platform.farming.interfaces.rest.resources.SelectCropTypeResource;

import java.util.Objects;

/**
 * Converts the select crop type request body into its command.
 */
public final class SelectCropTypeCommandFromResourceAssembler {

    private SelectCropTypeCommandFromResourceAssembler() {
    }

    /**
     * Builds the select crop type command.
     *
     * @param cropCampaignId the campaign id from the path
     * @param resource       the request body
     * @return the command
     */
    public static SelectCropTypeCommand toCommandFromResource(Long cropCampaignId, SelectCropTypeResource resource) {
        Objects.requireNonNull(resource, "Select crop type request body is required.");
        return new SelectCropTypeCommand(cropCampaignId,
                CropTypeFromResourceAssembler.toCropTypeFromResource(resource.cropType()));
    }
}
