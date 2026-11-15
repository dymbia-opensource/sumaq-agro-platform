package com.dymbia.sumaqagro.platform.certification.domain.model.valueobjects;

import java.math.BigDecimal;

/**
 * A value between zero and one hundred, inclusive.
 */
public record Percentage(BigDecimal value) {
    public Percentage {
        if (value == null || value.signum() < 0 || value.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new IllegalArgumentException("Percentage must be between 0 and 100");
        }
    }
}
