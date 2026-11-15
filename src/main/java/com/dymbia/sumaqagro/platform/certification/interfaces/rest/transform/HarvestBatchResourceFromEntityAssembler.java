package com.dymbia.sumaqagro.platform.certification.interfaces.rest.transform;

import com.dymbia.sumaqagro.platform.certification.domain.model.aggregates.HarvestBatch;
import com.dymbia.sumaqagro.platform.certification.interfaces.rest.resources.HarvestBatchResource;

import java.util.Objects;

/**
 * Converts the HarvestBatch aggregate into its resource.
 */
public final class HarvestBatchResourceFromEntityAssembler {

    private HarvestBatchResourceFromEntityAssembler() {
    }

    public static HarvestBatchResource toResourceFromEntity(HarvestBatch entity) {
        Objects.requireNonNull(entity, "Harvest batch is required.");
        return new HarvestBatchResource(
                entity.getId(),
                entity.getBatchCode(),
                entity.getCooperativeId(),
                entity.getMemberId(),
                entity.getPlotId(),
                entity.getCropType().name(),
                entity.getHarvestedOn(),
                entity.getRegisteredAt(),
                entity.getStatus().name(),
                entity.getNetWeightKg(),
                entity.getSampleIdentifier(),
                entity.getSampleWeightKg(),
                entity.getFirstCaliberPercentage(),
                entity.getSecondCaliberPercentage(),
                entity.getThirdCaliberPercentage(),
                entity.getPestDamagePercentage(),
                entity.getCoffeeCuppingScore(),
                entity.isCoffeeFermentDefect(),
                entity.getQualityScore(),
                entity.getQualityCategory() == null ? null : entity.getQualityCategory().name());
    }
}
