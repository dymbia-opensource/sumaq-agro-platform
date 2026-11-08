package com.dymbia.sumaqagro.platform.farming.domain.repositories;

import com.dymbia.sumaqagro.platform.farming.domain.model.aggregates.CropCampaign;
import com.dymbia.sumaqagro.platform.farming.domain.model.valueobjects.CampaignStatus;
import com.dymbia.sumaqagro.platform.farming.domain.model.valueobjects.FieldPlotId;

import java.util.List;
import java.util.Optional;

/**
 * Repository port of the crop campaign aggregate.
 * The domain says what it needs and the infrastructure layer implements it.
 */
public interface CropCampaignRepository {

    /**
     * Finds a crop campaign by its id.
     *
     * @param id the campaign id
     * @return the campaign, or an empty optional
     */
    Optional<CropCampaign> findById(Long id);

    /**
     * Finds all the campaigns of a field plot.
     *
     * @param fieldPlotId the field plot id
     * @return the campaigns of the plot, empty if there are none
     */
    List<CropCampaign> findAllByFieldPlotId(FieldPlotId fieldPlotId);

    /**
     * Checks whether a field plot has a campaign in the given status.
     *
     * @param fieldPlotId the field plot id
     * @param status      the campaign status
     * @return true if the plot has a campaign in that status
     */
    boolean existsByFieldPlotIdAndStatus(FieldPlotId fieldPlotId, CampaignStatus status);

    /**
     * Saves the crop campaign and publishes the domain events it registered.
     *
     * @param cropCampaign the campaign to save
     * @return the saved campaign, with its id
     */
    CropCampaign save(CropCampaign cropCampaign);
}
