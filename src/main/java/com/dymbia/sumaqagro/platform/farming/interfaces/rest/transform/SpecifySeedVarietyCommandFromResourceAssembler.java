package com.dymbia.sumaqagro.platform.farming.interfaces.rest.transform;

import com.dymbia.sumaqagro.platform.farming.domain.model.commands.SpecifySeedVarietyCommand;
import com.dymbia.sumaqagro.platform.farming.domain.model.valueobjects.SeedVariety;
import com.dymbia.sumaqagro.platform.farming.interfaces.rest.resources.SpecifySeedVarietyResource;

import java.util.Objects;

/**
 * Converts the specify seed variety request body into its command.
 */
public final class SpecifySeedVarietyCommandFromResourceAssembler {

    private SpecifySeedVarietyCommandFromResourceAssembler() {
    }

    /**
     * Builds the specify seed variety command.
     *
     * @param cropCampaignId the campaign id from the path
     * @param resource       the request body
     * @return the command
     */
    public static SpecifySeedVarietyCommand toCommandFromResource(Long cropCampaignId,
                                                                  SpecifySeedVarietyResource resource) {
        Objects.requireNonNull(resource, "Specify seed variety request body is required.");
        return new SpecifySeedVarietyCommand(cropCampaignId, new SeedVariety(resource.seedVariety()),
                resource.customSeedVariety());
    }
}
