package com.dymbia.sumaqagro.platform.profiles.interfaces.rest.transform;

import com.dymbia.sumaqagro.platform.profiles.domain.model.commands.RegisterCooperativeCommand;
import com.dymbia.sumaqagro.platform.profiles.interfaces.rest.resources.RegisterCooperativeResource;

public final class RegisterCooperativeCommandFromResourceAssembler {

    private RegisterCooperativeCommandFromResourceAssembler() {
    }

    public static RegisterCooperativeCommand toCommandFromResource(RegisterCooperativeResource resource) {
        return new RegisterCooperativeCommand(
                resource.ruc(),
                resource.businessName(),
                resource.region(),
                resource.managerUserId(),
                resource.institutionalEmail(),
                resource.legalRepresentative(),
                resource.headquartersAddress(),
                resource.switchboardPhone());
    }
}
