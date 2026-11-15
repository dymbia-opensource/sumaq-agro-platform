package com.dymbia.sumaqagro.platform.certification.domain.model.commands;

import com.dymbia.sumaqagro.platform.certification.domain.model.valueobjects.PotatoCaliberDistribution;

/**
 * Command to record the potato caliber distribution of a harvest batch.
 *
 * @param harvestBatchId        identity of the batch
 * @param distribution          caliber and pest-damage percentages
 * @since 1.0
 */
public record GradePotatoCalibersCommand(
        Long harvestBatchId,
        PotatoCaliberDistribution distribution
) {
    public GradePotatoCalibersCommand {
        if (harvestBatchId == null || harvestBatchId <= 0) {
            throw new IllegalArgumentException("Harvest batch id must be greater than zero");
        }
        if (distribution == null) {
            throw new IllegalArgumentException("Potato caliber distribution is required");
        }
    }
}
