package com.dymbia.sumaqagro.platform.profiles.interfaces.rest.transform;

import com.dymbia.sumaqagro.platform.profiles.domain.model.entities.AgronomistInvitation;
import com.dymbia.sumaqagro.platform.profiles.interfaces.rest.resources.AgronomistInvitationResource;

public final class AgronomistInvitationResourceFromEntityAssembler {

    private AgronomistInvitationResourceFromEntityAssembler() {
    }

    public static AgronomistInvitationResource toResourceFromEntity(AgronomistInvitation entity) {
        return new AgronomistInvitationResource(
                entity.getId(),
                entity.getCooperativeId(),
                entity.getEmail(),
                entity.getStatus().name(),
                entity.getSentAt());
    }
}
