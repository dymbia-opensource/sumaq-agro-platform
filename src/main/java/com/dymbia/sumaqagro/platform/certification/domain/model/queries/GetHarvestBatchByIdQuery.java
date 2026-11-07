package com.dymbia.sumaqagro.platform.certification.domain.model.queries;

/**
 * Query to retrieve a harvest batch by its identity.
 *
 * @param harvestBatchId harvest batch identifier
 * @since 1.0
 */
public record GetHarvestBatchByIdQuery(Long harvestBatchId) {

    public GetHarvestBatchByIdQuery {
        if (harvestBatchId == null || harvestBatchId <= 0) {
            throw new IllegalArgumentException("Harvest batch id must be greater than zero");
        }
    }
}
