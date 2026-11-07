package com.dymbia.sumaqagro.platform.farming.domain.model.queries;

/**
 * Query to get all the crop campaigns of a field plot.
 *
 * @param fieldPlotId the field plot id
 * @since 1.0
 */
public record GetAllCropCampaignsByFieldPlotIdQuery(Long fieldPlotId) {
    public GetAllCropCampaignsByFieldPlotIdQuery {
        if (fieldPlotId == null || fieldPlotId <= 0) {
            throw new IllegalArgumentException("crop.campaign.error.plotId.invalid");
        }
    }
}
