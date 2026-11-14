package com.dymbia.sumaqagro.platform.farming.application.commandservices;

import com.dymbia.sumaqagro.platform.farming.domain.model.commands.CloseCropCampaignCommand;
import com.dymbia.sumaqagro.platform.farming.domain.model.commands.StartCropCampaignCommand;
import com.dymbia.sumaqagro.platform.farming.domain.model.commands.UpdateCropCampaignCommand;
import com.dymbia.sumaqagro.platform.shared.application.result.ApplicationError;
import com.dymbia.sumaqagro.platform.shared.application.result.Result;

/**
 * Use cases that change crop campaigns. Each one returns the id of the campaign it changed.
 */
public interface CropCampaignCommandService {
    /**
     * Starts a crop campaign on a field plot.
     *
     * @param command the start crop campaign command
     * @return the id of the new campaign, or the error that stopped it
     */
    Result<Long, ApplicationError> handle(StartCropCampaignCommand command);

    /**
     * Changes the crop type, the seed variety or the sowing date of a campaign in progress.
     *
     * @param command the update crop campaign command
     * @return the id of the campaign, or the error that stopped it
     */
    Result<Long, ApplicationError> handle(UpdateCropCampaignCommand command);

    /**
     * Finishes a campaign in progress.
     *
     * @param command the close crop campaign command
     * @return the id of the campaign, or the error that stopped it
     */
    Result<Long, ApplicationError> handle(CloseCropCampaignCommand command);
}
