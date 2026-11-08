package com.dymbia.sumaqagro.platform.profiles.interfaces.rest.transform;

import com.dymbia.sumaqagro.platform.profiles.domain.model.commands.UpdateProfileCommand;
import com.dymbia.sumaqagro.platform.profiles.domain.model.valueobjects.PreferredLanguage;
import com.dymbia.sumaqagro.platform.profiles.interfaces.rest.resources.UpdateProfileResource;

import java.util.Objects;

/**
 * Converts an UpdateProfileResource into an UpdateProfileCommand.
 *
 * @since 1.0
 */
public final class UpdateProfileCommandFromResourceAssembler {

    private UpdateProfileCommandFromResourceAssembler() {
    }

    public static UpdateProfileCommand toCommandFromResource(Long userId, UpdateProfileResource resource) {
        Objects.requireNonNull(resource, "Update profile request body is required.");

        PreferredLanguage language = null;
        if (resource.preferredLanguage() != null && !resource.preferredLanguage().isBlank()) {
            language = PreferredLanguage.valueOf(resource.preferredLanguage().toUpperCase());
        }

        return new UpdateProfileCommand(
                userId,
                resource.firstName(),
                resource.lastName(),
                resource.phoneNumber(),
                resource.photoUrl(),
                language
        );
    }
}