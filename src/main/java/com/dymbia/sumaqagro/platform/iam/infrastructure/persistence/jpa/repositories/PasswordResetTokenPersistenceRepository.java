package com.dymbia.sumaqagro.platform.iam.infrastructure.persistence.jpa.repositories;

import com.dymbia.sumaqagro.platform.iam.infrastructure.persistence.jpa.entities.PasswordResetTokenPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.time.Instant;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Spring Data repository for stored reset tokens.
 */
public interface PasswordResetTokenPersistenceRepository
        extends JpaRepository<PasswordResetTokenPersistenceEntity, Long> {

    Optional<PasswordResetTokenPersistenceEntity> findByTokenHash(String tokenHash);

    @Modifying
    @Query("""
            update PasswordResetTokenPersistenceEntity token
               set token.consumedAt = :consumedAt, token.updatedAt = :consumedAt
             where token.tokenHash = :tokenHash
               and token.consumedAt is null
               and token.expiresAt > :consumedAt
            """)
    int consumeIfUsable(@Param("tokenHash") String tokenHash, @Param("consumedAt") Instant consumedAt);
}
