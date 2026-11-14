package com.dymbia.sumaqagro.platform.farming.application.queryservices;

import com.dymbia.sumaqagro.platform.farming.domain.model.aggregates.CropCampaign;
import com.dymbia.sumaqagro.platform.farming.domain.model.queries.GetAllCropCampaignsByFieldPlotIdQuery;
import com.dymbia.sumaqagro.platform.farming.domain.model.queries.GetCropCampaignByIdQuery;

import java.util.List;
import java.util.Optional;

/**
 * Use cases that read crop campaigns.
 */
public interface CropCampaignQueryService {
    /**
     * Gets a crop campaign by its id.
     *
     * @param query the query with the campaign id
     * @return the campaign, or an empty optional
     */
    Optional<CropCampaign> handle(GetCropCampaignByIdQuery query);

    /**
     * Gets all the crop campaigns of a field plot.
     *
     * @param query the query with the field plot id
     * @return the campaigns of the plot, empty if there are none
     */
    List<CropCampaign> handle(GetAllCropCampaignsByFieldPlotIdQuery query);
}
