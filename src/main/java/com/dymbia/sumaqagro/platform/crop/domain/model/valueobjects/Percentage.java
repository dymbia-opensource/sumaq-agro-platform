package com.dymbia.sumaqagro.platform.crop.domain.model.valueobjects;

/**
 * A percentage value.
 * Accepts finite values between 0 and 100, inclusive.
 */
public record Percentage(double value) {

    public Percentage {
        if (!Double.isFinite(value) || value < 0.0 || value > 100.0) {
            throw new IllegalArgumentException(
                    "Percentage must be a finite number between 0 and 100"
            );
        }
    }
}
