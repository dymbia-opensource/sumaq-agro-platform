package com.dymbia.sumaqagro.platform.iam.domain.repositories;

import com.dymbia.sumaqagro.platform.iam.domain.model.entities.PasswordResetToken;

import java.util.Optional;
import java.time.Instant;

/**
 * Persistence contract for password reset tokens.
 */
public interface PasswordResetTokenRepository {

    /**
     * Stores a newly issued token.
     */
    void insert(PasswordResetToken token);

    /**
     * Looks up the stored token by its SHA-256 digest.
     */
    Optional<PasswordResetToken> findByTokenHash(String tokenHash);

    /** Consumes an unexpired token exactly once. */
    boolean consumeIfUsable(String tokenHash, Instant consumedAt);
}
