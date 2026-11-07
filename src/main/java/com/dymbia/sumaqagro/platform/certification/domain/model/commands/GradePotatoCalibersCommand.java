package com.dymbia.sumaqagro.platform.certification.domain.model.commands;

import java.math.BigDecimal;

/**
 * Command to record the potato caliber distribution of a harvest batch.
 *
 * @param harvestBatchId        identity of the batch
 * @param firstPercentage       first-grade share
 * @param secondPercentage      second-grade share
 * @param thirdPercentage       third-grade share
 * @param pestDamagePercentage  pest-damage share
 * @since 1.0
 */
public record GradePotatoCalibersCommand(
        Long harvestBatchId,
        BigDecimal firstPercentage,
        BigDecimal secondPercentage,
        BigDecimal thirdPercentage,
        BigDecimal pestDamagePercentage
) {
    private static final BigDecimal ONE_HUNDRED = BigDecimal.valueOf(100);

    public GradePotatoCalibersCommand {
        if (harvestBatchId == null || harvestBatchId <= 0) {
            throw new IllegalArgumentException("Harvest batch id must be greater than zero");
        }
        requirePercentage(firstPercentage, "First-caliber percentage");
        requirePercentage(secondPercentage, "Second-caliber percentage");
        requirePercentage(thirdPercentage, "Third-caliber percentage");
        requirePercentage(pestDamagePercentage, "Pest-damage percentage");
        if (firstPercentage.add(secondPercentage).add(thirdPercentage).compareTo(ONE_HUNDRED) != 0) {
            throw new IllegalArgumentException("Potato caliber percentages must add up to 100");
        }
    }

    private static void requirePercentage(BigDecimal value, String label) {
        if (value == null || value.signum() < 0 || value.compareTo(ONE_HUNDRED) > 0) {
            throw new IllegalArgumentException(label + " must be between 0 and 100");
        }
    }
}
