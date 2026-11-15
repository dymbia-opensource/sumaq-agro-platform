package com.dymbia.sumaqagro.platform.certification.interfaces.rest.resources;

import java.math.BigDecimal;

/**
 * Request body used to weigh a harvest batch.
 */
public record WeighHarvestBatchResource(BigDecimal grossWeightKg, BigDecimal tareKg) {
}
