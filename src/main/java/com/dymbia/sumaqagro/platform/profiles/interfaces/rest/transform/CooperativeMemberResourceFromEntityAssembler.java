package com.dymbia.sumaqagro.platform.profiles.interfaces.rest.transform;

import com.dymbia.sumaqagro.platform.profiles.domain.model.entities.CooperativeMember;
import com.dymbia.sumaqagro.platform.profiles.interfaces.rest.resources.CooperativeMemberResource;

import java.util.Objects;

/**
 * Converts a CooperativeMember entity into its resource representation.
 *
 * @since 1.0
 */
public final class CooperativeMemberResourceFromEntityAssembler {

    private CooperativeMemberResourceFromEntityAssembler() {
    }

    public static CooperativeMemberResource toResourceFromEntity(CooperativeMember entity) {
        Objects.requireNonNull(entity, "Cooperative member is required.");
        return new CooperativeMemberResource(
                entity.getId(),
                entity.getCooperativeId(),
                entity.getDni().value(),
                entity.getName().firstName(),
                entity.getName().lastName(),
                entity.getCommunity(),
                entity.getJoinedAt()
        );
    }
}