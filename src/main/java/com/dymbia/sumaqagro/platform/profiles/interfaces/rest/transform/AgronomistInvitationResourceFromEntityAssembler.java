package com.dymbia.sumaqagro.platform.profiles.interfaces.rest.transform;

import com.dymbia.sumaqagro.platform.profiles.domain.model.entities.AgronomistInvitation;
import com.dymbia.sumaqagro.platform.profiles.interfaces.rest.resources.AgronomistInvitationResource;

import java.util.Objects;

/**
 * Converts an AgronomistInvitation entity into its resource representation.
 *
 * @since 1.0
 */
public final class AgronomistInvitationResourceFromEntityAssembler {

    private AgronomistInvitationResourceFromEntityAssembler() {
    }

    public static AgronomistInvitationResource toResourceFromEntity(AgronomistInvitation entity) {
        Objects.requireNonNull(entity, "Agronomist invitation is required.");
        return new AgronomistInvitationResource(
                entity.getId(),
                entity.getCooperative().getId(),
                entity.getEmail(),
                entity.getStatus().name(),
                entity.getSentAt()
        );
    }
}