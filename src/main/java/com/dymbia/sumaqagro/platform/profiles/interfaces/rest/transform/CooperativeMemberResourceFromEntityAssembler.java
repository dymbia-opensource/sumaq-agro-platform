package com.dymbia.sumaqagro.platform.profiles.interfaces.rest.transform;

import com.dymbia.sumaqagro.platform.profiles.domain.model.entities.CooperativeMember;
import com.dymbia.sumaqagro.platform.profiles.interfaces.rest.resources.CooperativeMemberResource;

public final class CooperativeMemberResourceFromEntityAssembler {

    private CooperativeMemberResourceFromEntityAssembler() {
    }

    public static CooperativeMemberResource toResourceFromEntity(CooperativeMember entity) {
        return new CooperativeMemberResource(
                entity.getId(),
                entity.getCooperativeId(),
                entity.getDni().value(),
                entity.getName().firstName(),
                entity.getName().lastName(),
                entity.getFullName(),
                entity.getCommunity(),
                entity.getJoinedAt(),
                entity.isActive(),
                entity.getUserId());
    }
}
