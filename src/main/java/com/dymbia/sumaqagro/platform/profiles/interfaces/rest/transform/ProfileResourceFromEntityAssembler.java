package com.dymbia.sumaqagro.platform.profiles.interfaces.rest.transform;

import com.dymbia.sumaqagro.platform.profiles.domain.model.aggregates.Profile;
import com.dymbia.sumaqagro.platform.profiles.interfaces.rest.resources.ProfileResource;

import java.util.Objects;

/**
 * Converts a Profile aggregate into its resource representation.
 *
 * @since 1.0
 */
public final class ProfileResourceFromEntityAssembler {

    private ProfileResourceFromEntityAssembler() {
    }

    public static ProfileResource toResourceFromEntity(Profile entity) {
        Objects.requireNonNull(entity, "Profile is required.");
        return new ProfileResource(
                entity.getId(),
                entity.getUserId(),
                entity.getName().firstName(),
                entity.getName().lastName(),
                entity.getPhoneNumber() != null ? entity.getPhoneNumber().value() : null,
                entity.getPhotoUrl(),
                entity.getPreferredLanguage().name()
        );
    }
}