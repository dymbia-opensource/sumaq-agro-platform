package com.dymbia.sumaqagro.platform.farming.interfaces.rest.transform;

import com.dymbia.sumaqagro.platform.farming.domain.model.aggregates.CropCampaign;
import com.dymbia.sumaqagro.platform.farming.interfaces.rest.resources.CropCampaignResource;

import java.util.Objects;

/**
 * Converts a crop campaign aggregate into its response body.
 */
public final class CropCampaignResourceFromEntityAssembler {

    private CropCampaignResourceFromEntityAssembler() {
    }

    /**
     * Builds the crop campaign response body.
     *
     * @param entity the crop campaign
     * @return the response body
     */
    public static CropCampaignResource toResourceFromEntity(CropCampaign entity) {
        Objects.requireNonNull(entity, "Crop campaign is required.");
        return new CropCampaignResource(
                entity.getId(),
                entity.getFieldPlotIdValue(),
                entity.getSeason(),
                entity.getCropType().name(),
                entity.getSeedVariety().value(),
                entity.isSeedVarietyCustom(),
                entity.getSowingDate(),
                entity.getStatus().name());
    }
}
