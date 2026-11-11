package com.dymbia.sumaqagro.platform.certification.domain.model.valueobjects;

import java.math.BigDecimal;

/**
 * Potato caliber shares and the percentage of pest damage.
 */
public record PotatoCaliberDistribution(
        Percentage firstPercentage,
        Percentage secondPercentage,
        Percentage thirdPercentage,
        Percentage pestDamagePercentage
) {
    private static final BigDecimal ONE_HUNDRED = BigDecimal.valueOf(100);

    public PotatoCaliberDistribution {
        if (firstPercentage == null || secondPercentage == null || thirdPercentage == null
                || pestDamagePercentage == null) {
            throw new IllegalArgumentException("All potato caliber percentages are required");
        }
        if (firstPercentage.value().add(secondPercentage.value()).add(thirdPercentage.value())
                .compareTo(ONE_HUNDRED) != 0) {
            throw new IllegalArgumentException("Potato caliber percentages must add up to 100");
        }
    }

    public static PotatoCaliberDistribution from(BigDecimal firstPercentage, BigDecimal secondPercentage,
                                                 BigDecimal thirdPercentage, BigDecimal pestDamagePercentage) {
        return new PotatoCaliberDistribution(
                new Percentage(firstPercentage),
                new Percentage(secondPercentage),
                new Percentage(thirdPercentage),
                new Percentage(pestDamagePercentage));
    }
}
