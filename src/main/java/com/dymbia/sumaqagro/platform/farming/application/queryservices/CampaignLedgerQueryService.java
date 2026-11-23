package com.dymbia.sumaqagro.platform.farming.application.queryservices;

import com.dymbia.sumaqagro.platform.farming.domain.model.aggregates.CampaignLedger;
import com.dymbia.sumaqagro.platform.farming.domain.model.queries.GetCampaignLedgerByCropCampaignIdQuery;
import com.dymbia.sumaqagro.platform.farming.domain.model.queries.GetCampaignLedgerByIdQuery;

import java.util.Optional;

/**
 * Query service of the cost ledger aggregate.
 *
 * @since 1.1
 */
public interface CampaignLedgerQueryService {

    /**
     * Finds a cost ledger by its id.
     *
     * @param query the ledger id
     * @return the ledger, or empty if it does not exist
     */
    Optional<CampaignLedger> handle(GetCampaignLedgerByIdQuery query);

    /**
     * Finds the cost ledger of a crop campaign.
     *
     * @param query the campaign id
     * @return the ledger, or empty if the campaign has none
     */
    Optional<CampaignLedger> handle(GetCampaignLedgerByCropCampaignIdQuery query);
}
