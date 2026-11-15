package com.dymbia.sumaqagro.platform.profiles.interfaces.rest.transform;

import com.dymbia.sumaqagro.platform.profiles.domain.model.commands.UpdateCooperativeCommand;
import com.dymbia.sumaqagro.platform.profiles.interfaces.rest.resources.UpdateCooperativeResource;

public final class UpdateCooperativeCommandFromResourceAssembler {

    private UpdateCooperativeCommandFromResourceAssembler() {
    }

    public static UpdateCooperativeCommand toCommandFromResource(Long cooperativeId, UpdateCooperativeResource resource) {
        return new UpdateCooperativeCommand(
                cooperativeId,
                resource.ruc(),
                resource.businessName(),
                resource.region(),
                resource.institutionalEmail(),
                resource.legalRepresentative(),
                resource.headquartersAddress(),
                resource.switchboardPhone());
    }
}
