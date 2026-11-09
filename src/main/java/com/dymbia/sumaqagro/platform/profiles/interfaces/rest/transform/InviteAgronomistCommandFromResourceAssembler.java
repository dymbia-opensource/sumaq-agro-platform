package com.dymbia.sumaqagro.platform.profiles.interfaces.rest.transform;

import com.dymbia.sumaqagro.platform.profiles.domain.model.commands.InviteAgronomistCommand;
import com.dymbia.sumaqagro.platform.profiles.interfaces.rest.resources.InviteAgronomistResource;

import java.util.Objects;

/**
 * Converts an InviteAgronomistResource into an InviteAgronomistCommand.
 *
 * @since 1.0
 */
public final class InviteAgronomistCommandFromResourceAssembler {

    private InviteAgronomistCommandFromResourceAssembler() {
    }

    public static InviteAgronomistCommand toCommandFromResource(Long cooperativeId,
                                                                InviteAgronomistResource resource) {
        Objects.requireNonNull(resource, "Invite agronomist request body is required.");
        return new InviteAgronomistCommand(
                cooperativeId,
                resource.email()
        );
    }
}