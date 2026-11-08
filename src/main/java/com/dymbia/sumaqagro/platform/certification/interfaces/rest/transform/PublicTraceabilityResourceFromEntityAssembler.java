package com.dymbia.sumaqagro.platform.certification.interfaces.rest.transform;

import com.dymbia.sumaqagro.platform.certification.application.queryservices.HarvestCertificationQueryService.PublicTraceability;
import com.dymbia.sumaqagro.platform.certification.interfaces.rest.resources.PublicTraceabilityResource;

import java.util.Objects;

/**
 * Converts the public traceability view into its resource.
 */
public final class PublicTraceabilityResourceFromEntityAssembler {

    private PublicTraceabilityResourceFromEntityAssembler() {
    }

    public static PublicTraceabilityResource toResourceFromEntity(PublicTraceability entity) {
        Objects.requireNonNull(entity, "Public traceability is required.");
        var origin = entity.origin();
        return new PublicTraceabilityResource(
                entity.certificateNumber(),
                entity.status().name(),
                entity.authentic(),
                entity.batchCode(),
                entity.cropType().name(),
                entity.qualityCategory().name(),
                entity.qualityScore(),
                origin == null ? null : origin.plotName(),
                origin == null ? null : origin.region(),
                origin == null ? null : origin.seedVariety());
    }
}
