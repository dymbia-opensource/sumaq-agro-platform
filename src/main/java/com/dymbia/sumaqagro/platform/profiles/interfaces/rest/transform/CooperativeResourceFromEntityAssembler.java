package com.dymbia.sumaqagro.platform.profiles.interfaces.rest.transform;

import com.dymbia.sumaqagro.platform.profiles.domain.model.aggregates.Cooperative;
import com.dymbia.sumaqagro.platform.profiles.interfaces.rest.resources.CooperativeResource;

public final class CooperativeResourceFromEntityAssembler {

    private CooperativeResourceFromEntityAssembler() {
    }

    public static CooperativeResource toResourceFromEntity(Cooperative entity) {
        return new CooperativeResource(
                entity.getId(),
                entity.getRuc().value(),
                entity.getBusinessName(),
                entity.getRegion(),
                entity.getManagerUserId(),
                entity.getInstitutionalEmail(),
                entity.getLegalRepresentative(),
                entity.getHeadquartersAddress(),
                entity.getSwitchboardPhone());
    }
}
