package com.dymbia.sumaqagro.platform.farming.application.commandservices;

import com.dymbia.sumaqagro.platform.farming.domain.model.commands.CloseCropCampaignCommand;
import com.dymbia.sumaqagro.platform.farming.domain.model.commands.RecordSowingDateCommand;
import com.dymbia.sumaqagro.platform.farming.domain.model.commands.SelectCropTypeCommand;
import com.dymbia.sumaqagro.platform.farming.domain.model.commands.SpecifySeedVarietyCommand;
import com.dymbia.sumaqagro.platform.farming.domain.model.commands.StartCropCampaignCommand;
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
     * Changes the crop of a campaign in progress.
     *
     * @param command the select crop type command
     * @return the id of the campaign, or the error that stopped it
     */
    Result<Long, ApplicationError> handle(SelectCropTypeCommand command);

    /**
     * Changes the seed variety of a campaign in progress.
     *
     * @param command the specify seed variety command
     * @return the id of the campaign, or the error that stopped it
     */
    Result<Long, ApplicationError> handle(SpecifySeedVarietyCommand command);

    /**
     * Records the sowing date of a campaign in progress.
     *
     * @param command the record sowing date command
     * @return the id of the campaign, or the error that stopped it
     */
    Result<Long, ApplicationError> handle(RecordSowingDateCommand command);

    /**
     * Finishes a campaign in progress.
     *
     * @param command the close crop campaign command
     * @return the id of the campaign, or the error that stopped it
     */
    Result<Long, ApplicationError> handle(CloseCropCampaignCommand command);
}
