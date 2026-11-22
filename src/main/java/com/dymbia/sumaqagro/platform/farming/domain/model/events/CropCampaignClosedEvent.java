package com.dymbia.sumaqagro.platform.farming.domain.model.events;

/**
 * Raised when a crop campaign finishes.
 * The cost ledger of the campaign is frozen when this event is handled (US-35).
 *
 * @param cropCampaignId the id of the finished campaign
 */
public record CropCampaignClosedEvent(Long cropCampaignId) {
}