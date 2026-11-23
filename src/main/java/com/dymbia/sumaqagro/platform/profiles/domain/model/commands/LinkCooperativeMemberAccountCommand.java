package com.dymbia.sumaqagro.platform.profiles.domain.model.commands;

/**
 * Command to link a member registered without an account to the producer account he signs in with.
 *
 * @param cooperativeId  identity of the cooperative
 * @param directorUserId identity of the authenticated director
 * @param memberId       identity of the member
 * @param email          email of the producer account
 * @since 1.2
 */
public record LinkCooperativeMemberAccountCommand(
        Long cooperativeId,
        long directorUserId,
        Long memberId,
        String email
) {
    public LinkCooperativeMemberAccountCommand {
        if (cooperativeId == null || cooperativeId <= 0) {
            throw new IllegalArgumentException("Cooperative id must be a positive number");
        }
        if (directorUserId <= 0) {
            throw new IllegalArgumentException("Director user id must be positive");
        }
        if (memberId == null || memberId <= 0) {
            throw new IllegalArgumentException("Member id must be a positive number");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email must not be null or blank");
        }
    }
}
