package com.dymbia.sumaqagro.platform.profiles.interfaces.rest.transform;

import com.dymbia.sumaqagro.platform.profiles.domain.model.aggregates.Profile;
import com.dymbia.sumaqagro.platform.profiles.interfaces.rest.resources.ProfileResource;

public final class ProfileResourceFromEntityAssembler {

    private ProfileResourceFromEntityAssembler() {
    }

    /**
     * @param entity the profile
     * @param email   the account email read from IAM (may be {@code null})
     * @param owner   whether the reader owns the profile: only he sees the phone, the email and the DNI
     */
    public static ProfileResource toResourceFromEntity(Profile entity, String email, boolean owner) {
        return new ProfileResource(
                entity.getId(),
                entity.getUserId(),
                entity.getName().firstName(),
                entity.getName().lastName(),
                entity.getFullName(),
                owner ? email : null,
                owner ? entity.getPhoneNumber() : null,
                entity.getPhotoUrl(),
                entity.getPreferredLanguage().name(),
                owner ? entity.getDni() : null);
    }
}
