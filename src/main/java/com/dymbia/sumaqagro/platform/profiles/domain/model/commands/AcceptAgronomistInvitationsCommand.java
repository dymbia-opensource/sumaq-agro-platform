package com.dymbia.sumaqagro.platform.profiles.domain.model.commands;

/**
 * Command to accept the pending invitations sent to the email of a new agronomist account.
 *
 * @param agronomistUserId IAM user id of the agronomist
 * @param email            email of the agronomist account
 * @since 1.2
 */
public record AcceptAgronomistInvitationsCommand(Long agronomistUserId, String email) {
    public AcceptAgronomistInvitationsCommand {
        if (agronomistUserId == null || agronomistUserId <= 0) {
            throw new IllegalArgumentException("Agronomist user id must be a positive number");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email must not be null or blank");
        }
    }
}
