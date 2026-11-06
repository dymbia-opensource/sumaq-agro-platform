package com.dymbia.sumaqagro.platform.iam.infrastructure.persistence.jpa.assemblers;

import com.dymbia.sumaqagro.platform.iam.domain.model.entities.Role;
import com.dymbia.sumaqagro.platform.iam.infrastructure.persistence.jpa.entities.RolePersistenceEntity;

public class RolePersistenceAssembler {
    private RolePersistenceAssembler() {
    }

    public static Role toDomainFromPersistence(RolePersistenceEntity entity) {
        return new Role(entity.getId(), entity.getName());
    }
}
