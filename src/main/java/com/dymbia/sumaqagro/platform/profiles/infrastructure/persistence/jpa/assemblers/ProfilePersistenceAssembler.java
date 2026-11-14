package com.dymbia.sumaqagro.platform.profiles.infrastructure.persistence.jpa.assemblers;

import com.dymbia.sumaqagro.platform.profiles.domain.model.aggregates.Profile;
import com.dymbia.sumaqagro.platform.profiles.domain.model.valueobjects.PersonName;
import com.dymbia.sumaqagro.platform.profiles.domain.model.valueobjects.PhoneNumber;
import com.dymbia.sumaqagro.platform.profiles.infrastructure.persistence.jpa.embeddables.PersonNamePersistenceEmbeddable;
import com.dymbia.sumaqagro.platform.profiles.infrastructure.persistence.jpa.entities.ProfilePersistenceEntity;

/**
 * Assembles between the Profile domain aggregate and its JPA persistence entity.
 */
public final class ProfilePersistenceAssembler {

    private ProfilePersistenceAssembler() {
    }

    /**
     * Converts a domain Profile into a persistence entity.
     *
     * @param domain the domain aggregate
     * @return the persistence entity
     */
    public static ProfilePersistenceEntity toPersistenceFromDomain(Profile domain) {
        var entity = new ProfilePersistenceEntity();
        entity.setId(domain.getId());
        entity.setUserId(domain.getUserId());
        entity.setName(toPersistenceFromDomain(domain.getName()));
        entity.setPhoneNumber(domain.getPhoneNumber());
        entity.setPhotoUrl(domain.getPhotoUrl());
        entity.setPreferredLanguage(domain.getPreferredLanguage());
        return entity;
    }

    /**
     * Converts a persistence entity into a domain Profile.
     *
     * @param entity the persistence entity
     * @return the domain aggregate
     */
    public static Profile toDomainFromPersistence(ProfilePersistenceEntity entity) {
        return Profile.reconstitute(
                entity.getId(),
                entity.getUserId(),
                toDomainFromPersistence(entity.getName()),
                entity.getPhoneNumber() != null ? new PhoneNumber(entity.getPhoneNumber()) : null,
                entity.getPhotoUrl(),
                entity.getPreferredLanguage()
        );
    }

    private static PersonNamePersistenceEmbeddable toPersistenceFromDomain(PersonName value) {
        return value == null ? null : new PersonNamePersistenceEmbeddable(value.firstName(), value.lastName());
    }

    private static PersonName toDomainFromPersistence(PersonNamePersistenceEmbeddable value) {
        return value == null ? null : new PersonName(value.getFirstName(), value.getLastName());
    }
}