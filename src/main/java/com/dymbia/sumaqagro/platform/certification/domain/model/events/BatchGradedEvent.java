package com.dymbia.sumaqagro.platform.certification.domain.model.events;

import com.dymbia.sumaqagro.platform.certification.domain.model.aggregates.HarvestBatch;

/**
 * Domain event raised when potato grading or coffee cupping finishes.
 * The batch graded handler uses it to assign the quality score.
 *
 * @param harvestBatchId identity of the graded batch, null before persistence
 * @param cropType       crop that was graded
 * @since 1.0
 */
public record BatchGradedEvent(Long harvestBatchId, HarvestBatch.CropType cropType) {
}
