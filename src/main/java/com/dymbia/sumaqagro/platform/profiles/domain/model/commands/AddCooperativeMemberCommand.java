package com.dymbia.sumaqagro.platform.profiles.domain.model.commands;

import java.time.LocalDate;

/**
 * Command to add a member to a cooperative.
 *
 * @param cooperativeId identity of the cooperative
 * @param directorUserId identity of the authenticated director
 * @param dni           DNI of the member (8 digits)
 * @param firstName     first name of the member
 * @param lastName      last name of the member
 * @param community     community or caserío of the member
 * @param joinedAt      date when the member joined the cooperative
 * @param userId        IAM user id of the member account, if known
 * @param email         email of the member account, resolved to {@code userId} by the application layer
 * @since 1.0
 */
public record AddCooperativeMemberCommand(
        Long cooperativeId,
        long directorUserId,
        String dni,
        String firstName,
        String lastName,
        String community,
        LocalDate joinedAt,
        Long userId,
        String email
) {
    public AddCooperativeMemberCommand {
        if (directorUserId <= 0) {
            throw new IllegalArgumentException("Director user id must be positive");
        }
        if (cooperativeId == null || cooperativeId <= 0) {
            throw new IllegalArgumentException("Cooperative id must be a positive number");
        }
        if (dni == null || dni.isBlank()) {
            throw new IllegalArgumentException("DNI must not be null or blank");
        }
        if (firstName == null || firstName.isBlank()) {
            throw new IllegalArgumentException("First name must not be null or blank");
        }
        if (lastName == null || lastName.isBlank()) {
            throw new IllegalArgumentException("Last name must not be null or blank");
        }
        if (community == null || community.isBlank()) {
            throw new IllegalArgumentException("Community must not be null or blank");
        }
        if (userId != null && userId <= 0) {
            throw new IllegalArgumentException("User id must be a positive number");
        }
        if (email != null && email.isBlank()) {
            email = null;
        }
        if (userId != null && email != null) {
            throw new IllegalArgumentException("Send the account email or the user id, not both");
        }
    }

    /**
     * Returns the same command linked to the account found for {@link #email()}.
     *
     * @param resolvedUserId IAM user id of the account
     * @return the command with the user id
     */
    public AddCooperativeMemberCommand withUserId(Long resolvedUserId) {
        return new AddCooperativeMemberCommand(cooperativeId, directorUserId, dni, firstName, lastName,
                community, joinedAt, resolvedUserId, null);
    }
}
