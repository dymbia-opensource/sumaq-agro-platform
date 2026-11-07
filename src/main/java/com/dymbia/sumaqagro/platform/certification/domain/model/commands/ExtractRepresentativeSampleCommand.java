package com.dymbia.sumaqagro.platform.certification.domain.model.commands;

import java.math.BigDecimal;

/**
 * Command to attach the representative sample of a harvest batch.
 *
 * @param harvestBatchId  identity of the batch
 * @param sampleIdentifier sample identifier
 * @param sampleWeightKg  sample mass in kilograms
 * @since 1.0
 */
public record ExtractRepresentativeSampleCommand(
        Long harvestBatchId,
        String sampleIdentifier,
        BigDecimal sampleWeightKg
) {
    public ExtractRepresentativeSampleCommand {
        if (harvestBatchId == null || harvestBatchId <= 0) {
            throw new IllegalArgumentException("Harvest batch id must be greater than zero");
        }
        if (sampleIdentifier == null || sampleIdentifier.isBlank()) {
            throw new IllegalArgumentException("Sample identifier is required");
        }
        if (sampleWeightKg == null || sampleWeightKg.signum() <= 0) {
            throw new IllegalArgumentException("Sample weight must be greater than zero");
        }
    }
}
