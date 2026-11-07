package com.dymbia.sumaqagro.platform.certification.domain.model.commands;

import java.math.BigDecimal;

/**
 * Command to record the specialty coffee cupping of a harvest batch.
 *
 * @param harvestBatchId identity of the batch
 * @param aroma           aroma score
 * @param flavor          flavor score
 * @param acidity         acidity score
 * @param body            body score
 * @param totalScore      total SCA score
 * @param fermentDefect   whether a ferment defect was found
 * @since 1.0
 */
public record CupCoffeeCommand(
        Long harvestBatchId,
        BigDecimal aroma,
        BigDecimal flavor,
        BigDecimal acidity,
        BigDecimal body,
        BigDecimal totalScore,
        boolean fermentDefect
) {
    private static final BigDecimal ONE_HUNDRED = BigDecimal.valueOf(100);

    public CupCoffeeCommand {
        if (harvestBatchId == null || harvestBatchId <= 0) {
            throw new IllegalArgumentException("Harvest batch id must be greater than zero");
        }
        requireScore(aroma, "Aroma");
        requireScore(flavor, "Flavor");
        requireScore(acidity, "Acidity");
        requireScore(body, "Body");
        requireScore(totalScore, "Total cupping score");
    }

    private static void requireScore(BigDecimal value, String label) {
        if (value == null || value.signum() < 0 || value.compareTo(ONE_HUNDRED) > 0) {
            throw new IllegalArgumentException(label + " must be between 0 and 100");
        }
    }
}
