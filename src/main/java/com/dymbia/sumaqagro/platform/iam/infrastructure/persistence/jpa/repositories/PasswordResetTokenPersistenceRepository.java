package com.dymbia.sumaqagro.platform.iam.infrastructure.persistence.jpa.repositories;

import com.dymbia.sumaqagro.platform.iam.infrastructure.persistence.jpa.entities.PasswordResetTokenPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Spring Data repository for stored reset tokens.
 */
public interface PasswordResetTokenPersistenceRepository
        extends JpaRepository<PasswordResetTokenPersistenceEntity, Long> {

    Optional<PasswordResetTokenPersistenceEntity> findByTokenHash(String tokenHash);
}