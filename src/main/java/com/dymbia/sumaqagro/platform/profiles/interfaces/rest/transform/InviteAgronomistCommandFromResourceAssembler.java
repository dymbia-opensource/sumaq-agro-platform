package com.dymbia.sumaqagro.platform.profiles.interfaces.rest.transform;

import com.dymbia.sumaqagro.platform.profiles.domain.model.commands.InviteAgronomistCommand;
import com.dymbia.sumaqagro.platform.profiles.interfaces.rest.resources.InviteAgronomistResource;

public final class InviteAgronomistCommandFromResourceAssembler {

    private InviteAgronomistCommandFromResourceAssembler() {
    }

    public static InviteAgronomistCommand toCommandFromResource(Long cooperativeId, InviteAgronomistResource resource) {
        return new InviteAgronomistCommand(cooperativeId, resource.email());
    }
}
