package com.dymbia.sumaqagro.platform.farming.domain.model.valueobjects;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

/**
 * Initial soil analysis of a field plot, from laboratory data.
 *
 * @param ph                      the soil pH, from 0 to 14
 * @param texture                 the soil texture
 * @param organicMatterPercentage the organic matter, from 0 to 100 %
 * @param recordedAt              the date of the analysis, not in the future
 * @since 1.0
 */
public record SoilBaseline(BigDecimal ph, SoilTexture texture, BigDecimal organicMatterPercentage,
                           LocalDate recordedAt) {
    private static final BigDecimal MAX_PH = BigDecimal.valueOf(14);
    private static final BigDecimal MAX_PERCENTAGE = BigDecimal.valueOf(100);

    /**
     * Validates the analysis. The pH and the organic matter are kept with 2 decimals.
     *
     * @throws IllegalArgumentException when a value is missing or out of range
     */
    public SoilBaseline {
        if (ph == null || ph.signum() < 0 || ph.compareTo(MAX_PH) > 0) {
            throw new IllegalArgumentException("field.plot.error.soil.ph.invalid");
        }
        if (texture == null) {
            throw new IllegalArgumentException("field.plot.error.soil.texture.required");
        }
        if (organicMatterPercentage == null || organicMatterPercentage.signum() < 0
                || organicMatterPercentage.compareTo(MAX_PERCENTAGE) > 0) {
            throw new IllegalArgumentException("field.plot.error.soil.organicMatter.invalid");
        }
        if (recordedAt == null || recordedAt.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("field.plot.error.soil.recordedAt.invalid");
        }
        ph = ph.setScale(2, RoundingMode.HALF_UP);
        organicMatterPercentage = organicMatterPercentage.setScale(2, RoundingMode.HALF_UP);
    }
}
