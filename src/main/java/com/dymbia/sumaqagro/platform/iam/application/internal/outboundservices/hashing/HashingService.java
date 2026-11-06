package com.dymbia.sumaqagro.platform.iam.application.internal.outboundservices.hashing;

/**
 * Defines the password hashing operations required by IAM application services.
 */
public interface HashingService {

    /**
     * Encodes a raw password for persistence.
     *
     * @param rawPassword the password to encode
     * @return the encoded password
     */
    String encode(CharSequence rawPassword);

    /**
     * Checks whether a raw password matches an encoded password.
     *
     * @param rawPassword the password supplied by the user
     * @param encodedPassword the stored password hash
     * @return {@code true} if the password matches
     */
    boolean matches(CharSequence rawPassword, String encodedPassword);
}