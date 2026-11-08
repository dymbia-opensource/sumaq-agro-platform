package com.dymbia.sumaqagro.platform.certification.interfaces.rest.resources;

import java.time.LocalDate;

/**
 * Request body used to register a harvest batch.
 */
public record RegisterHarvestBatchResource(
        String batchCode,
        Long cooperativeId,
        Long memberId,
        Long plotId,
        String cropType,
        LocalDate harvestedOn
) {
}
