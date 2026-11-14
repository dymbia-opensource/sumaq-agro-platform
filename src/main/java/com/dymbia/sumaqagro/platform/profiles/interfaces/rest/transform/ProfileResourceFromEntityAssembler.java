package com.dymbia.sumaqagro.platform.profiles.interfaces.rest.transform;

import com.dymbia.sumaqagro.platform.profiles.domain.model.aggregates.Profile;
import com.dymbia.sumaqagro.platform.profiles.interfaces.rest.resources.ProfileResource;

public final class ProfileResourceFromEntityAssembler {

    private ProfileResourceFromEntityAssembler() {
    }

    /**
     * @param entity the profile
     * @param email  the account email read from IAM (may be {@code null})
     */
    public static ProfileResource toResourceFromEntity(Profile entity, String email) {
        return new ProfileResource(
                entity.getId(),
                entity.getUserId(),
                entity.getName().firstName(),
                entity.getName().lastName(),
                entity.getFullName(),
                email,
                entity.getPhoneNumber(),
                entity.getPhotoUrl(),
                entity.getPreferredLanguage().name());
    }
}
