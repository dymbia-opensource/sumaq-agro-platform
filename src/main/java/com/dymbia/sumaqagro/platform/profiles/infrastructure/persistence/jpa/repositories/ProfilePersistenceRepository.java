package com.dymbia.sumaqagro.platform.profiles.infrastructure.persistence.jpa.repositories;

import com.dymbia.sumaqagro.platform.profiles.infrastructure.persistence.jpa.entities.ProfilePersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Provides Spring Data JPA access to persisted profiles.
 */
public interface ProfilePersistenceRepository extends JpaRepository<ProfilePersistenceEntity, Long> {

    /**
     * Finds the profile associated with an IAM user.
     *
     * @param userId the IAM user identifier
     * @return the profile, if one exists
     */
    Optional<ProfilePersistenceEntity> findByUserId(Long userId);

    /**
     * Checks whether an IAM user already has a profile.
     *
     * @param userId the IAM user identifier
     * @return {@code true} if a profile exists
     */
    boolean existsByUserId(Long userId);
}