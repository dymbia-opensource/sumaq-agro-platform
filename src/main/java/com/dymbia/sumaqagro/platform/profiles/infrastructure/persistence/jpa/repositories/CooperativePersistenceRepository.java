package com.dymbia.sumaqagro.platform.profiles.infrastructure.persistence.jpa.repositories;

import com.dymbia.sumaqagro.platform.profiles.domain.model.aggregates.Cooperative;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for the Cooperative aggregate.
 *
 * @since 1.0
 */
@Repository
public interface CooperativePersistenceRepository extends JpaRepository<Cooperative, Long> {

    Optional<Cooperative> findByRuc(String ruc);

    boolean existsByRuc(String ruc);
}