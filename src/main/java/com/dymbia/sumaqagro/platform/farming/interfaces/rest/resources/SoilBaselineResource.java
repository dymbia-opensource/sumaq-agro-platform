package com.dymbia.sumaqagro.platform.farming.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Initial soil analysis of a field plot, as the API sends and receives it.
 *
 * @param ph                      the soil pH, from 0 to 14
 * @param texture                 the soil texture ({@code SANDY}, {@code LOAMY}, {@code CLAY}, {@code SILTY},
 *                                {@code SANDY_LOAM} or {@code CLAY_LOAM})
 * @param organicMatterPercentage the organic matter, from 0 to 100 %
 * @param recordedAt              the date of the analysis, in {@code YYYY-MM-DD} format
 */
@Schema(name = "SoilBaseline", description = "Initial soil analysis of a field plot")
public record SoilBaselineResource(
        @Schema(description = "Soil pH, from 0 to 14", example = "6.50")
        BigDecimal ph,
        @Schema(description = "Soil texture", example = "LOAMY",
                allowableValues = {"SANDY", "LOAMY", "CLAY", "SILTY", "SANDY_LOAM", "CLAY_LOAM"})
        String texture,
        @Schema(description = "Organic matter, from 0 to 100 %", example = "3.20")
        BigDecimal organicMatterPercentage,
        @Schema(description = "Date of the analysis, not in the future", example = "2026-10-01")
        LocalDate recordedAt) {
}
