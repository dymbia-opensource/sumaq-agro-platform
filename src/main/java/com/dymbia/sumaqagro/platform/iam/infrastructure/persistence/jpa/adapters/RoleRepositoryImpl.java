package com.dymbia.sumaqagro.platform.iam.infrastructure.persistence.jpa.adapters;

import com.dymbia.sumaqagro.platform.iam.domain.model.entities.Role;
import com.dymbia.sumaqagro.platform.iam.domain.model.valueobjects.Roles;
import com.dymbia.sumaqagro.platform.iam.domain.repositories.RoleRepository;
import com.dymbia.sumaqagro.platform.iam.infrastructure.persistence.jpa.assemblers.RolePersistenceAssembler;
import com.dymbia.sumaqagro.platform.iam.infrastructure.persistence.jpa.repositories.RolePersistenceRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class RoleRepositoryImpl implements RoleRepository {

    private final RolePersistenceRepository persistenceRepository;

    public RoleRepositoryImpl(RolePersistenceRepository persistenceRepository) {
        this.persistenceRepository = persistenceRepository;
    }

    @Override
    public Optional<Role> findByName(Roles name) {
        return persistenceRepository.findByName(name)
                .map(RolePersistenceAssembler::toDomainFromPersistence);
    }
}