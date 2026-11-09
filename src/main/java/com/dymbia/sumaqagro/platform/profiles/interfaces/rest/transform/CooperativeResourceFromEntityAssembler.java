package com.dymbia.sumaqagro.platform.profiles.interfaces.rest.transform;

import com.dymbia.sumaqagro.platform.profiles.domain.model.aggregates.Cooperative;
import com.dymbia.sumaqagro.platform.profiles.interfaces.rest.resources.CooperativeResource;

import java.util.Objects;

/**
 * Converts a Cooperative aggregate into its resource representation.
 *
 * @since 1.0
 */

public final class CooperativeResourceFromEntityAssembler {
    private CooperativeResourceFromEntityAssembler() {
    }

    public static CooperativeResource toResourceFromEntity(Cooperative entity) {
        Objects.requireNonNull(entity, "Cooperative is required.");
        return new CooperativeResource(
                entity.getId(),
                entity.getRuc().value(),
                entity.getBusinessName(),
                entity.getRegion(),
                entity.getManagerUserId()
        );
    }
}
