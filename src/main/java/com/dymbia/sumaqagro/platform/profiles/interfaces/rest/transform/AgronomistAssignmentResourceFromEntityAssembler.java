package com.dymbia.sumaqagro.platform.profiles.interfaces.rest.transform;

import com.dymbia.sumaqagro.platform.profiles.domain.model.aggregates.AgronomistAssignment;
import com.dymbia.sumaqagro.platform.profiles.interfaces.rest.resources.AgronomistAssignmentResource;

import java.util.Objects;

/**
 * Converts an AgronomistAssignment aggregate into its resource representation.
 *
 * @since 1.0
 */
public final class AgronomistAssignmentResourceFromEntityAssembler {

    private AgronomistAssignmentResourceFromEntityAssembler() {
    }

    public static AgronomistAssignmentResource toResourceFromEntity(AgronomistAssignment entity) {
        Objects.requireNonNull(entity, "Agronomist assignment is required.");
        return new AgronomistAssignmentResource(
                entity.getId(),
                entity.getCooperativeId(),
                entity.getAgronomistUserId(),
                entity.getPlotId(),
                entity.getAssignedAt(),
                entity.isActive()
        );
    }
}