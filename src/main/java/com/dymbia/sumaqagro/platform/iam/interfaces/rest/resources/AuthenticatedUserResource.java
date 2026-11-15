package com.dymbia.sumaqagro.platform.iam.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * Represents the authenticated user returned by the token endpoint.
 *
 * @param id the user identifier
 * @param email the account email
 * @param roles the assigned role names
 * @param token the signed access token
 * @param fullName the name from the user profile, when present
 */
@Schema(name = "AuthenticatedUserResponse", description = "Authenticated user and access token")
public record AuthenticatedUserResource(
        Long id,
        String email,
        List<String> roles,
        String token,
        String fullName
) {

    /**
     * Returns a representation safe for logging.
     *
     * @return the resource name without the access token
     */
    @Override
    public String toString() {
        return "AuthenticatedUserResource";
    }
}