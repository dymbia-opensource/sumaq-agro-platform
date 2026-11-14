package com.dymbia.sumaqagro.platform.profiles.infrastructure.persistence.jpa.repositories;

import com.dymbia.sumaqagro.platform.profiles.infrastructure.persistence.jpa.entities.CooperativePersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Provides Spring Data JPA access to persisted cooperatives.
 */
public interface CooperativePersistenceRepository extends JpaRepository<CooperativePersistenceEntity, Long> {

    Optional<CooperativePersistenceEntity> findByRuc(String ruc);

    boolean existsByRuc(String ruc);
}