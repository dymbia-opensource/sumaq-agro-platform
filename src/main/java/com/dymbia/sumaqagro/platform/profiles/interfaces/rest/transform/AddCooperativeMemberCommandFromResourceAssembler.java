package com.dymbia.sumaqagro.platform.profiles.interfaces.rest.transform;

import com.dymbia.sumaqagro.platform.profiles.domain.model.commands.AddCooperativeMemberCommand;
import com.dymbia.sumaqagro.platform.profiles.interfaces.rest.resources.AddCooperativeMemberResource;

import java.util.Objects;

/**
 * Converts an AddCooperativeMemberResource into an AddCooperativeMemberCommand.
 *
 * @since 1.0
 */
public final class AddCooperativeMemberCommandFromResourceAssembler {

    private AddCooperativeMemberCommandFromResourceAssembler() {
    }

    public static AddCooperativeMemberCommand toCommandFromResource(Long cooperativeId,
                                                                    AddCooperativeMemberResource resource) {
        Objects.requireNonNull(resource, "Add cooperative member request body is required.");
        return new AddCooperativeMemberCommand(
                cooperativeId,
                resource.dni(),
                resource.firstName(),
                resource.lastName(),
                resource.community(),
                resource.joinedAt()
        );
    }
}