package com.dymbia.sumaqagro.platform.profiles.interfaces.rest.transform;

import com.dymbia.sumaqagro.platform.profiles.domain.model.commands.UpdateProfileCommand;
import com.dymbia.sumaqagro.platform.profiles.domain.model.valueobjects.PreferredLanguage;
import com.dymbia.sumaqagro.platform.profiles.interfaces.rest.resources.UpdateProfileResource;

import java.util.Locale;

public final class UpdateProfileCommandFromResourceAssembler {

    private UpdateProfileCommandFromResourceAssembler() {
    }

    public static UpdateProfileCommand toCommandFromResource(Long profileId, UpdateProfileResource resource) {
        PreferredLanguage language = null;
        if (resource.preferredLanguage() != null && !resource.preferredLanguage().isBlank()) {
            language = PreferredLanguage.valueOf(resource.preferredLanguage().trim().toUpperCase(Locale.ROOT));
        }
        return new UpdateProfileCommand(
                profileId,
                resource.firstName(),
                resource.lastName(),
                resource.phoneNumber(),
                resource.photoUrl(),
                language);
    }
}
