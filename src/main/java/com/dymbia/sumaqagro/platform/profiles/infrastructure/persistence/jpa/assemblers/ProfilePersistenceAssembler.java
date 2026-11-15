package com.dymbia.sumaqagro.platform.profiles.infrastructure.persistence.jpa.assemblers;

import com.dymbia.sumaqagro.platform.profiles.domain.model.aggregates.Profile;
import com.dymbia.sumaqagro.platform.profiles.domain.model.valueobjects.PersonName;
import com.dymbia.sumaqagro.platform.profiles.domain.model.valueobjects.PhoneNumber;
import com.dymbia.sumaqagro.platform.profiles.infrastructure.persistence.jpa.embeddables.PersonNamePersistenceEmbeddable;
import com.dymbia.sumaqagro.platform.profiles.infrastructure.persistence.jpa.entities.ProfilePersistenceEntity;

/**
 * Translates between the {@link Profile} aggregate and {@link ProfilePersistenceEntity}.
 *
 * @since 1.0
 */
public final class ProfilePersistenceAssembler {

    private ProfilePersistenceAssembler() {
    }

    public static Profile toDomainFromPersistence(ProfilePersistenceEntity entity) {
        if (entity == null) return null;
        return Profile.reconstitute(
                entity.getId(),
                entity.getUserId(),
                toDomainFromPersistence(entity.getName()),
                entity.getPhoneNumber() != null ? new PhoneNumber(entity.getPhoneNumber()) : null,
                entity.getPhotoUrl(),
                entity.getPreferredLanguage());
    }

    public static ProfilePersistenceEntity toPersistenceFromDomain(Profile profile) {
        var entity = new ProfilePersistenceEntity();
        if (profile.getId() != null) {
            entity.setId(profile.getId());
        }
        entity.setUserId(profile.getUserId());
        entity.setName(toPersistenceFromDomain(profile.getName()));
        entity.setPhoneNumber(profile.getPhoneNumber());
        entity.setPhotoUrl(profile.getPhotoUrl());
        entity.setPreferredLanguage(profile.getPreferredLanguage());
        return entity;
    }

    private static PersonNamePersistenceEmbeddable toPersistenceFromDomain(PersonName value) {
        return value == null ? null : new PersonNamePersistenceEmbeddable(value.firstName(), value.lastName());
    }

    private static PersonName toDomainFromPersistence(PersonNamePersistenceEmbeddable value) {
        return value == null ? null : new PersonName(value.getFirstName(), value.getLastName());
    }
}
