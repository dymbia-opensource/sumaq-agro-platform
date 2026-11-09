package com.dymbia.sumaqagro.platform.profiles.interfaces.rest.transform;

import com.dymbia.sumaqagro.platform.profiles.domain.model.commands.RegisterCooperativeCommand;
import com.dymbia.sumaqagro.platform.profiles.interfaces.rest.resources.RegisterCooperativeResource;

import java.util.Objects;

/**
 * Converts a RegisterCooperativeResource into a RegisterCooperativeCommand.
 *
 * @since 1.0
 */
public final class RegisterCooperativeCommandFromResourceAssembler {

    private RegisterCooperativeCommandFromResourceAssembler() {
    }

    public static RegisterCooperativeCommand toCommandFromResource(RegisterCooperativeResource resource) {
        Objects.requireNonNull(resource, "Register cooperative request body is required.");
        return new RegisterCooperativeCommand(
                resource.ruc(),
                resource.businessName(),
                resource.region(),
                resource.managerUserId()
        );
    }
}