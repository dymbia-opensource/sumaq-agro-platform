package com.dymbia.sumaqagro.platform.certification.domain.model.queries;

/**
 * Query for the quality certificate of a harvest batch.
 *
 * @param harvestBatchId the certified batch
 * @since 1.1
 */
public record GetQualityCertificateByHarvestBatchIdQuery(Long harvestBatchId) {

    public GetQualityCertificateByHarvestBatchIdQuery {
        if (harvestBatchId == null || harvestBatchId <= 0) {
            throw new IllegalArgumentException("Harvest batch id must be greater than zero");
        }
    }
}
