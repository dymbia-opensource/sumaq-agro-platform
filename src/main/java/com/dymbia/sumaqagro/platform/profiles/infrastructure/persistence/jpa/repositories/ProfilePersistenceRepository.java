package com.dymbia.sumaqagro.platform.profiles.infrastructure.persistence.jpa.repositories;

import com.dymbia.sumaqagro.platform.profiles.domain.model.aggregates.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for the Profile aggregate.
 *
 * @since 1.0
 */
@Repository
public interface ProfilePersistenceRepository extends JpaRepository<Profile, Long> {

    Optional<Profile> findByUserId(Long userId);

    boolean existsByUserId(Long userId);
}