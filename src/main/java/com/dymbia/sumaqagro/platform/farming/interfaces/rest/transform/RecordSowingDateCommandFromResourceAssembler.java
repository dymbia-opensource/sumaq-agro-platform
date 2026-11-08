package com.dymbia.sumaqagro.platform.farming.interfaces.rest.transform;

import com.dymbia.sumaqagro.platform.farming.domain.model.commands.RecordSowingDateCommand;
import com.dymbia.sumaqagro.platform.farming.interfaces.rest.resources.RecordSowingDateResource;

import java.util.Objects;

/**
 * Converts the record sowing date request body into its command.
 */
public final class RecordSowingDateCommandFromResourceAssembler {

    private RecordSowingDateCommandFromResourceAssembler() {
    }

    /**
     * Builds the record sowing date command.
     *
     * @param cropCampaignId the campaign id from the path
     * @param resource       the request body
     * @return the command
     */
    public static RecordSowingDateCommand toCommandFromResource(Long cropCampaignId,
                                                                RecordSowingDateResource resource) {
        Objects.requireNonNull(resource, "Record sowing date request body is required.");
        return new RecordSowingDateCommand(cropCampaignId, resource.sowingDate());
    }
}
