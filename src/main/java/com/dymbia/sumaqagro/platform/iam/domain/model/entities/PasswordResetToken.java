package com.dymbia.sumaqagro.platform.iam.domain.model.entities;

import java.time.Instant;
import java.util.Objects;

/**
 * A password reset token bound to a single IAM account.
 * Stores the token digest and tracks its expiration and consumption.
 */
public final class PasswordResetToken {

    private final long userId;
    private final String tokenHash;
    private final Instant expiresAt;
    private Instant consumedAt;

    public PasswordResetToken(long userId, String tokenHash, Instant expiresAt) {
        if (userId <= 0) {
            throw new IllegalArgumentException("User ID must be positive");
        }
        if (tokenHash == null || !tokenHash.matches("[0-9a-f]{64}")) {
            throw new IllegalArgumentException("Token hash must be a SHA-256 hex digest");
        }

        this.userId = userId;
        this.tokenHash = tokenHash;
        this.expiresAt = Objects.requireNonNull(expiresAt, "Expiration is required");
    }

    public long getUserId() {
        return userId;
    }

    public String getTokenHash() {
        return tokenHash;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public Instant getConsumedAt() {
        return consumedAt;
    }

    /**
     * Checks whether the token can be used at the supplied instant.
     */
    public boolean canBeUsedAt(Instant now) {
        Objects.requireNonNull(now, "Current time is required");
        return consumedAt == null && now.isBefore(expiresAt);
    }

    /**
     * Marks a valid token as used.
     */
    public void consume(Instant now) {
        if (!canBeUsedAt(now)) {
            throw new IllegalStateException("Password reset token is expired or already used");
        }
        consumedAt = now;
    }

    /**
     * Restores a stored token, including its consumption state.
     */
    public static PasswordResetToken reconstitute(
            long userId, String tokenHash, Instant expiresAt, Instant consumedAt
    ) {
        var token = new PasswordResetToken(userId, tokenHash, expiresAt);
        token.consumedAt = consumedAt;
        return token;
    }
}