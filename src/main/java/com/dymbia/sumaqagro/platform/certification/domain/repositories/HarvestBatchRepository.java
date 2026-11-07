package com.dymbia.sumaqagro.platform.certification.domain.repositories;

import com.dymbia.sumaqagro.platform.certification.domain.model.aggregates.HarvestBatch;

import java.util.Optional;

/**
 * Repository port of the harvest batch aggregate.
 * The domain says what it needs and the infrastructure layer implements it.
 *
 * @since 1.0
 */
public interface HarvestBatchRepository {

    /**
     * Finds a harvest batch by its id.
     *
     * @param id the batch id
     * @return the batch, or an empty optional
     */
    Optional<HarvestBatch> findById(Long id);

    /**
     * Finds a harvest batch by its unique code.
     *
     * @param batchCode the batch code
     * @return the batch, or an empty optional
     */
    Optional<HarvestBatch> findByBatchCode(String batchCode);

    /**
     * Checks whether a batch code is already in use.
     *
     * @param batchCode the batch code
     * @return {@code true} when the code already belongs to a batch
     */
    boolean existsByBatchCode(String batchCode);

    /**
     * Saves the harvest batch.
     *
     * @param harvestBatch the batch to save
     * @return the saved batch, with its id
     */
    HarvestBatch save(HarvestBatch harvestBatch);
}
