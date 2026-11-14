package com.dymbia.sumaqagro.platform.certification.domain.model.commands;

import com.dymbia.sumaqagro.platform.certification.domain.model.valueobjects.CoffeeCuppingScores;

/**
 * Command to record the specialty coffee cupping of a harvest batch.
 *
 * @param harvestBatchId identity of the batch
 * @param scores          coffee cupping scores
 * @param fermentDefect   whether a ferment defect was found
 * @since 1.0
 */
public record CupCoffeeCommand(
        Long harvestBatchId,
        CoffeeCuppingScores scores,
        boolean fermentDefect
) {
    public CupCoffeeCommand {
        if (harvestBatchId == null || harvestBatchId <= 0) {
            throw new IllegalArgumentException("Harvest batch id must be greater than zero");
        }
        if (scores == null) {
            throw new IllegalArgumentException("Coffee cupping scores are required");
        }
    }
}
