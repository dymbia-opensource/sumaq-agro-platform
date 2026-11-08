package com.dymbia.sumaqagro.platform.certification.interfaces.rest.resources;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

/**
 * Harvest batch returned by the API.
 */
public record HarvestBatchResource(
        Long id,
        String batchCode,
        Long cooperativeId,
        Long memberId,
        Long plotId,
        String cropType,
        LocalDate harvestedOn,
        Instant registeredAt,
        String status,
        BigDecimal netWeightKg,
        String sampleIdentifier,
        BigDecimal sampleWeightKg,
        BigDecimal firstCaliberPercentage,
        BigDecimal secondCaliberPercentage,
        BigDecimal thirdCaliberPercentage,
        BigDecimal pestDamagePercentage,
        BigDecimal coffeeCuppingScore,
        boolean coffeeFermentDefect,
        BigDecimal qualityScore,
        String qualityCategory
) {
}
