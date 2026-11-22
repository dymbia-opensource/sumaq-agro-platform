package com.dymbia.sumaqagro.platform.certification.domain.model.queries;

/**
 * Query for every harvest batch received by a cooperative.
 *
 * @param cooperativeId the cooperative that received the batches
 * @since 1.1
 */
public record GetAllHarvestBatchesByCooperativeIdQuery(Long cooperativeId) {

    public GetAllHarvestBatchesByCooperativeIdQuery {
        if (cooperativeId == null || cooperativeId <= 0) {
            throw new IllegalArgumentException("Cooperative id must be greater than zero");
        }
    }
}
