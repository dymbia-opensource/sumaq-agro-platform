package com.dymbia.sumaqagro.platform.iam.application.internal.outboundservices.tokens;

/**
 * Defines token issuance and validation operations required by IAM.
 */
public interface TokenService {

    /**
     * Generates a signed token for an authenticated user.
     *
     * @param email the user's normalized email
     * @return the signed JWT
     */
    String generateToken(String email);

    /**
     * Extracts the user's email after verifying the signed token.
     *
     * @param token the signed JWT
     * @return the email stored as the token subject
     * @throws IllegalArgumentException if the token lacks an email or expiration
     */
    String getEmailFromToken(String token);

    /**
     * Checks the token signature, expiration and subject.
     *
     * @param token the JWT to validate
     * @return {@code true} if the token is valid
     */
    boolean validateToken(String token);
}