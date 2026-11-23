package com.dymbia.sumaqagro.platform.profiles.domain.model.commands;

/**
 * Command to invite an agronomist to a cooperative.
 *
 * @param cooperativeId  identity of the cooperative
 * @param directorUserId identity of the authenticated director
 * @param email          email of the agronomist to invite
 * @since 1.0
 */
public record InviteAgronomistCommand(
        Long cooperativeId,
        long directorUserId,
        String email
) {
    public InviteAgronomistCommand {
        if (directorUserId <= 0) {
            throw new IllegalArgumentException("Director user id must be positive");
        }
        if (cooperativeId == null || cooperativeId <= 0) {
            throw new IllegalArgumentException("Cooperative id must be a positive number");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email must not be null or blank");
        }
    }
}