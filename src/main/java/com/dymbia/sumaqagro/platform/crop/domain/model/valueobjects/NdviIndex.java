package com.dymbia.sumaqagro.platform.crop.domain.model.valueobjects;

/**
 * Normalized Difference Vegetation Index.
 * Accepts finite values between -1 and 1, inclusive.
 */
public record NdviIndex(double value) {

    public NdviIndex {
        if (!Double.isFinite(value) || value < -1.0 || value > 1.0) {
            throw new IllegalArgumentException(
                    "NDVI must be a finite number between -1 and 1"
            );
        }
    }
}