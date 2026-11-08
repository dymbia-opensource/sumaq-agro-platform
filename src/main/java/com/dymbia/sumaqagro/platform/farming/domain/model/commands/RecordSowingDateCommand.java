package com.dymbia.sumaqagro.platform.farming.domain.model.commands;

import java.time.LocalDate;

/**
 * Command to record the sowing date of a crop campaign. It starts the satellite monitoring (policy P4).
 *
 * @param cropCampaignId the campaign id
 * @param sowingDate     the sowing date
 * @since 1.0
 */
public record RecordSowingDateCommand(Long cropCampaignId, LocalDate sowingDate) {
    /**
     * Validates the command parameters.
     *
     * @throws IllegalArgumentException when the campaign id or the sowing date is invalid
     */
    public RecordSowingDateCommand {
        if (cropCampaignId == null || cropCampaignId <= 0) {
            throw new IllegalArgumentException("crop.campaign.error.id.invalid");
        }
        if (sowingDate == null) {
            throw new IllegalArgumentException("crop.campaign.error.sowingDate.required");
        }
    }
}
