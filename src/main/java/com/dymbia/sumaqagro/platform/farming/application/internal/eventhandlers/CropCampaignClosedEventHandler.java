package com.dymbia.sumaqagro.platform.farming.application.internal.eventhandlers;

import com.dymbia.sumaqagro.platform.farming.application.commandservices.CampaignLedgerCommandService;
import com.dymbia.sumaqagro.platform.farming.domain.model.commands.FreezeCampaignLedgerCommand;
import com.dymbia.sumaqagro.platform.farming.domain.model.events.CropCampaignClosedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/**
 * Freezes the cost ledger when its crop campaign finishes (US-35).
 *
 * @since 1.1
 */
@Service
public class CropCampaignClosedEventHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(CropCampaignClosedEventHandler.class);

    private final CampaignLedgerCommandService campaignLedgerCommandService;

    public CropCampaignClosedEventHandler(CampaignLedgerCommandService campaignLedgerCommandService) {
        this.campaignLedgerCommandService = campaignLedgerCommandService;
    }

    @EventListener
    public void on(CropCampaignClosedEvent event) {
        var result = campaignLedgerCommandService.handle(new FreezeCampaignLedgerCommand(event.cropCampaignId()));
        if (result.isFailure()) {
            LOGGER.warn("The cost ledger of campaign {} was not frozen: {}", event.cropCampaignId(), result);
        }
    }
}
