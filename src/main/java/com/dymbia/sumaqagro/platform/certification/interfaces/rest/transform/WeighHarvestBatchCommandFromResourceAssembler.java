package com.dymbia.sumaqagro.platform.certification.interfaces.rest.transform;

import com.dymbia.sumaqagro.platform.certification.domain.model.commands.WeighHarvestBatchCommand;
import com.dymbia.sumaqagro.platform.certification.interfaces.rest.resources.WeighHarvestBatchResource;

import java.util.Objects;

public final class WeighHarvestBatchCommandFromResourceAssembler {

    private WeighHarvestBatchCommandFromResourceAssembler() {
    }

    public static WeighHarvestBatchCommand toCommandFromResource(Long harvestBatchId, WeighHarvestBatchResource resource) {
        Objects.requireNonNull(resource, "Weigh harvest batch request body is required.");
        return new WeighHarvestBatchCommand(harvestBatchId, resource.grossWeightKg(), resource.tareKg());
    }
}
