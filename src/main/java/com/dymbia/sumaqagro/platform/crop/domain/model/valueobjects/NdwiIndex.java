package com.dymbia.sumaqagro.platform.crop.domain.model.valueobjects;

/**
 * Normalized Difference Water Index.
 * Accepts finite values between -1 and 1, inclusive.
 */
public record NdwiIndex(double value) {

    public NdwiIndex {
        if (!Double.isFinite(value) || value < -1.0 || value > 1.0) {
            throw new IllegalArgumentException(
                    "NDWI must be a finite number between -1 and 1"
            );
        }
    }
}