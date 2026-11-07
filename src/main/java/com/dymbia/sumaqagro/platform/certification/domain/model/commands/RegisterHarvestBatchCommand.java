package com.dymbia.sumaqagro.platform.certification.domain.model.commands;

import com.dymbia.sumaqagro.platform.certification.domain.model.aggregates.HarvestBatch;

import java.time.LocalDate;

/**
 * Command to register a harvest batch received at the collection center.
 *
 * @param batchCode     unique batch code
 * @param cooperativeId cooperative that receives the batch
 * @param memberId      delivering member
 * @param plotId        source plot
 * @param cropType      evaluated crop
 * @param harvestedOn   harvest date
 * @since 1.0
 */
public record RegisterHarvestBatchCommand(
        String batchCode,
        Long cooperativeId,
        Long memberId,
        Long plotId,
        HarvestBatch.CropType cropType,
        LocalDate harvestedOn
) {
    public RegisterHarvestBatchCommand {
        if (batchCode == null || batchCode.isBlank() || batchCode.trim().length() > 40) {
            throw new IllegalArgumentException("Batch code is required and must not exceed 40 characters");
        }
        if (cooperativeId == null || cooperativeId <= 0) {
            throw new IllegalArgumentException("Cooperative id must be greater than zero");
        }
        if (memberId == null || memberId <= 0) {
            throw new IllegalArgumentException("Member id must be greater than zero");
        }
        if (plotId == null || plotId <= 0) {
            throw new IllegalArgumentException("Plot id must be greater than zero");
        }
        if (cropType == null) {
            throw new IllegalArgumentException("Crop type is required");
        }
        if (harvestedOn == null) {
            throw new IllegalArgumentException("Harvest date is required");
        }
    }
}
