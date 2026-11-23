package com.dymbia.sumaqagro.platform.farming.domain.model.events;

import com.dymbia.sumaqagro.platform.farming.domain.model.valueobjects.CropType;

/**
 * Raised when a crop campaign is saved for the first time.
 * The cost ledger of the campaign is opened when this event is handled (US-36).
 *
 * @param cropCampaignId the id of the new campaign
 * @param cropType       the crop of the campaign, used to choose the yield unit
 */
public record CropCampaignStartedEvent(Long cropCampaignId, CropType cropType) {
}
