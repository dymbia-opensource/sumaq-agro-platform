package com.dymbia.sumaqagro.platform.certification.interfaces.rest.resources;

import java.math.BigDecimal;

/**
 * Request body used to record the coffee cupping of a harvest batch.
 */
public record CupCoffeeResource(
        BigDecimal aroma,
        BigDecimal flavor,
        BigDecimal acidity,
        BigDecimal body,
        BigDecimal totalScore,
        boolean fermentDefect
) {
}
