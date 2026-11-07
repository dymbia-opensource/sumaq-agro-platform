package com.dymbia.sumaqagro.platform.certification.domain.model.commands;

import java.math.BigDecimal;

/**
 * Command to record the net weight of a harvest batch.
 * Pass a tare of zero when the scale reading is already the net weight.
 *
 * @param harvestBatchId identity of the batch
 * @param grossWeightKg  scale reading, including packaging
 * @param tareKg         packaging weight to subtract
 * @since 1.0
 */
public record WeighHarvestBatchCommand(Long harvestBatchId, BigDecimal grossWeightKg, BigDecimal tareKg) {

    public WeighHarvestBatchCommand {
        if (harvestBatchId == null || harvestBatchId <= 0) {
            throw new IllegalArgumentException("Harvest batch id must be greater than zero");
        }
        if (grossWeightKg == null || tareKg == null || tareKg.signum() < 0 || grossWeightKg.compareTo(tareKg) <= 0) {
            throw new IllegalArgumentException("Gross weight must be greater than a non-negative tare");
        }
    }
}
