package com.dymbia.sumaqagro.platform.farming.application.internal.eventhandlers;

import com.dymbia.sumaqagro.platform.farming.application.commandservices.CampaignLedgerCommandService;
import com.dymbia.sumaqagro.platform.farming.domain.model.commands.OpenCampaignLedgerCommand;
import com.dymbia.sumaqagro.platform.farming.domain.model.events.CropCampaignStartedEvent;
import com.dymbia.sumaqagro.platform.farming.domain.model.valueobjects.YieldUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/**
 * Opens the cost ledger when a crop campaign starts (US-36).
 * <p>
 * The yield unit depends on the crop: sacks for coffee, quintals for the rest.
 * </p>
 *
 * @since 1.1
 */
@Service
public class CropCampaignStartedEventHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(CropCampaignStartedEventHandler.class);

    private final CampaignLedgerCommandService campaignLedgerCommandService;

    public CropCampaignStartedEventHandler(CampaignLedgerCommandService campaignLedgerCommandService) {
        this.campaignLedgerCommandService = campaignLedgerCommandService;
    }

    @EventListener
    public void on(CropCampaignStartedEvent event) {
        var command = new OpenCampaignLedgerCommand(event.cropCampaignId(), YieldUnit.forCropType(event.cropType()));
        var result = campaignLedgerCommandService.handle(command);
        if (result.isFailure()) {
            LOGGER.warn("The cost ledger of campaign {} was not opened: {}", event.cropCampaignId(), result);
        }
    }
}
