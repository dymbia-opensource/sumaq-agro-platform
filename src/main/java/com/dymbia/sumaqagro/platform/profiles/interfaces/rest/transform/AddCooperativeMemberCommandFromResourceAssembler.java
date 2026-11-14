package com.dymbia.sumaqagro.platform.profiles.interfaces.rest.transform;

import com.dymbia.sumaqagro.platform.profiles.domain.model.commands.AddCooperativeMemberCommand;
import com.dymbia.sumaqagro.platform.profiles.interfaces.rest.resources.AddCooperativeMemberResource;

public final class AddCooperativeMemberCommandFromResourceAssembler {

    private AddCooperativeMemberCommandFromResourceAssembler() {
    }

    public static AddCooperativeMemberCommand toCommandFromResource(Long cooperativeId,
                                                                    AddCooperativeMemberResource resource) {
        return new AddCooperativeMemberCommand(
                cooperativeId,
                resource.dni(),
                resource.firstName(),
                resource.lastName(),
                resource.community(),
                resource.joinedAt(),
                resource.userId());
    }
}
