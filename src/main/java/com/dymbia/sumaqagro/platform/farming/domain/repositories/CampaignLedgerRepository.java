package com.dymbia.sumaqagro.platform.farming.domain.repositories;

import com.dymbia.sumaqagro.platform.farming.domain.model.aggregates.CampaignLedger;
import com.dymbia.sumaqagro.platform.farming.domain.model.valueobjects.CropCampaignId;

import java.util.Optional;

/**
 * Repository port of the cost ledger aggregate.
 * <p>
 * The domain only knows this contract; the JPA adapter of the infrastructure implements it.
 * A ledger is never deleted: when its campaign finishes, it is frozen.
 * </p>
 *
 * @since 1.1
 */
public interface CampaignLedgerRepository {

    /**
     * Finds a cost ledger by its id
     *
     * @param id the ledger id
     * @return the ledger, or empty if it does not exist
     */
    Optional<CampaignLedger> findById(Long id);

    /**
     * Finds the cost ledger of a crop campaign
     *
     * @param cropCampaignId the campaign that owns the ledger
     * @return the ledger, or empty if the campaign has none
     */
    Optional<CampaignLedger> findByCropCampaignId(CropCampaignId cropCampaignId);

    /**
     * Checks if a crop campaign already has a cost ledger
     *
     * @param cropCampaignId the campaign that owns the ledger
     * @return true if the campaign has a ledger
     */
    boolean existsByCropCampaignId(CropCampaignId cropCampaignId);

    /**
     * Saves a cost ledger with its expenses. If it has no id, it is inserted and receives one
     *
     * @param campaignLedger the ledger to save
     * @return the saved ledger with its id
     */
    CampaignLedger save(CampaignLedger campaignLedger);
}
