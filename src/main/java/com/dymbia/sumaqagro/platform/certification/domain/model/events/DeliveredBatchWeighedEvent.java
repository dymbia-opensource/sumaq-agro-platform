package com.dymbia.sumaqagro.platform.certification.domain.model.events;

import java.math.BigDecimal;

/**
 * Domain event raised when a harvest batch is weighed at the collection center.
 * Field Management uses it to record the actual yield of the plot.
 *
 * @param harvestBatchId identity of the weighed batch, null before persistence
 * @param plotId         source plot
 * @param netWeightKg    delivered net weight in kilograms
 * @since 1.0
 */
public record DeliveredBatchWeighedEvent(Long harvestBatchId, Long plotId, BigDecimal netWeightKg) {
}
