package com.dymbia.sumaqagro.platform.iam.infrastructure.persistence.jpa.assemblers;

import com.dymbia.sumaqagro.platform.iam.domain.model.aggregates.User;
import com.dymbia.sumaqagro.platform.iam.domain.model.valueobjects.EmailAddress;
import com.dymbia.sumaqagro.platform.iam.infrastructure.persistence.jpa.entities.UserPersistenceEntity;

import java.util.stream.Collectors;

/**
 * This class is responsible for converting between the domain model and the persistence model for users.
 */
public final class UserPersistenceAssembler {

    private UserPersistenceAssembler() {
    }

    /**
     * This method is responsible for converting a UserPersistenceEntity to a User domain model.
     * @param entity The UserPersistenceEntity.
     * @return The User domain model.
     */
    public static User toDomainFromPersistence(UserPersistenceEntity entity) {
        var roles = entity.getRoles().stream()
                .map(RolePersistenceAssembler::toDomainFromPersistence)
                .collect(Collectors.toSet());

        return User.reconstitute(
                entity.getId(),
                new EmailAddress(entity.getEmail()),
                entity.getPasswordHash(),
                roles
        );
    }

    public static UserPersistenceEntity toPersistenceFromDomain(User user) {
        var entity = new UserPersistenceEntity();

        if (user.getId() != null) {
            entity.setId(user.getId());
        }

        entity.setEmail(user.getEmail().value());
        entity.setPasswordHash(user.getPasswordHash());
        entity.setRoles(user.getRoles().stream()
                .map(RolePersistenceAssembler::toPersistenceFromDomain)
                .collect(Collectors.toSet()));

        return entity;
    }
}
