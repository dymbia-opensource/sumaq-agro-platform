package com.dymbia.sumaqagro.platform.certification.interfaces.rest.transform;

import com.dymbia.sumaqagro.platform.certification.domain.model.commands.ExtractRepresentativeSampleCommand;
import com.dymbia.sumaqagro.platform.certification.interfaces.rest.resources.ExtractRepresentativeSampleResource;

import java.util.Objects;

public final class ExtractRepresentativeSampleCommandFromResourceAssembler {

    private ExtractRepresentativeSampleCommandFromResourceAssembler() {
    }

    public static ExtractRepresentativeSampleCommand toCommandFromResource(Long harvestBatchId,
                                                                          ExtractRepresentativeSampleResource resource) {
        Objects.requireNonNull(resource, "Extract representative sample request body is required.");
        return new ExtractRepresentativeSampleCommand(
                harvestBatchId, resource.sampleIdentifier(), resource.sampleWeightKg());
    }
}
