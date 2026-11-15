package com.dymbia.sumaqagro.platform.profiles.domain.model.queries;

/**
 * Query to retrieve a profile by the user identity.
 *
 * @param userId identity of the user
 * @since 1.0
 */
public record GetProfileByUserIdQuery(Long userId) {
    public GetProfileByUserIdQuery {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("User id must be a positive number");
        }
    }
}