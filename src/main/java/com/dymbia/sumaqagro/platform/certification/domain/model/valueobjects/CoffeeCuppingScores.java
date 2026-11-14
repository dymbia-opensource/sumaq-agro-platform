package com.dymbia.sumaqagro.platform.certification.domain.model.valueobjects;

import java.math.BigDecimal;

/**
 * Scores recorded during a specialty coffee cupping.
 */
public record CoffeeCuppingScores(
        Percentage aroma,
        Percentage flavor,
        Percentage acidity,
        Percentage body,
        Percentage totalScore
) {
    public CoffeeCuppingScores {
        if (aroma == null || flavor == null || acidity == null || body == null || totalScore == null) {
            throw new IllegalArgumentException("All coffee cupping scores are required");
        }
    }

    public static CoffeeCuppingScores from(BigDecimal aroma, BigDecimal flavor, BigDecimal acidity,
                                           BigDecimal body, BigDecimal totalScore) {
        return new CoffeeCuppingScores(
                new Percentage(aroma),
                new Percentage(flavor),
                new Percentage(acidity),
                new Percentage(body),
                new Percentage(totalScore));
    }
}
