package com.dymbia.sumaqagro.platform.certification.domain.model.valueobjects;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CertificationValueObjectsTests {

    @Test
    void shouldAcceptPercentagesWithinRange() {
        assertEquals(new BigDecimal("0"), new Percentage(BigDecimal.ZERO).value());
        assertEquals(new BigDecimal("100"), new Percentage(BigDecimal.valueOf(100)).value());
    }

    @Test
    void shouldRejectPercentagesOutsideRange() {
        assertThrows(IllegalArgumentException.class, () -> new Percentage(null));
        assertThrows(IllegalArgumentException.class, () -> new Percentage(BigDecimal.valueOf(-1)));
        assertThrows(IllegalArgumentException.class, () -> new Percentage(BigDecimal.valueOf(100.01)));
    }

    @Test
    void shouldRequirePotatoCalibersToTotalOneHundredPercent() {
        var distribution = PotatoCaliberDistribution.from(
                BigDecimal.valueOf(40),
                BigDecimal.valueOf(35),
                BigDecimal.valueOf(25),
                BigDecimal.valueOf(5));

        assertEquals(BigDecimal.valueOf(40), distribution.firstPercentage().value());
        assertThrows(IllegalArgumentException.class, () -> PotatoCaliberDistribution.from(
                BigDecimal.valueOf(40),
                BigDecimal.valueOf(35),
                BigDecimal.valueOf(20),
                BigDecimal.valueOf(5)));
    }

    @Test
    void shouldValidateEveryCoffeeCuppingScore() {
        var scores = CoffeeCuppingScores.from(
                BigDecimal.valueOf(8),
                BigDecimal.valueOf(8),
                BigDecimal.valueOf(8),
                BigDecimal.valueOf(8),
                BigDecimal.valueOf(85));

        assertEquals(BigDecimal.valueOf(85), scores.totalScore().value());
        assertThrows(IllegalArgumentException.class, () -> CoffeeCuppingScores.from(
                BigDecimal.valueOf(8),
                BigDecimal.valueOf(8),
                BigDecimal.valueOf(8),
                BigDecimal.valueOf(101),
                BigDecimal.valueOf(85)));
    }
}
