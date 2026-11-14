package com.dymbia.sumaqagro.platform.farming.interfaces.rest.resources;

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
public record SoilBaselineResource(BigDecimal ph, String texture, BigDecimal organicMatterPercentage,
                                   LocalDate recordedAt) {
}
