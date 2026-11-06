package com.dymbia.sumaqagro.platform.iam.infrastructure.persistence.jpa.repositories;

import com.dymbia.sumaqagro.platform.iam.domain.model.valueobjects.Roles;
import com.dymbia.sumaqagro.platform.iam.infrastructure.persistence.jpa.entities.RolePersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RolePersistenceRepository extends JpaRepository<RolePersistenceEntity, Long> {
    Optional<RolePersistenceEntity> findByName(Roles name);
}
