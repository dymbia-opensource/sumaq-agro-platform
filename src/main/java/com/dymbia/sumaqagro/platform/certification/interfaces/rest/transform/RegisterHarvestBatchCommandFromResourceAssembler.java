package com.dymbia.sumaqagro.platform.certification.interfaces.rest.transform;

import com.dymbia.sumaqagro.platform.certification.domain.model.commands.RegisterHarvestBatchCommand;
import com.dymbia.sumaqagro.platform.certification.domain.model.valueobjects.CropType;
import com.dymbia.sumaqagro.platform.certification.interfaces.rest.resources.RegisterHarvestBatchResource;

import java.util.Objects;

public final class RegisterHarvestBatchCommandFromResourceAssembler {

    private RegisterHarvestBatchCommandFromResourceAssembler() {
    }

    public static RegisterHarvestBatchCommand toCommandFromResource(RegisterHarvestBatchResource resource) {
        Objects.requireNonNull(resource, "Register harvest batch request body is required.");
        if (resource.cropType() == null || resource.cropType().isBlank()) {
            throw new IllegalArgumentException("Crop type is required");
        }
        return new RegisterHarvestBatchCommand(
                resource.batchCode(),
                resource.cooperativeId(),
                resource.memberId(),
                resource.plotId(),
                CropType.valueOf(resource.cropType()),
                resource.harvestedOn());
    }
}
