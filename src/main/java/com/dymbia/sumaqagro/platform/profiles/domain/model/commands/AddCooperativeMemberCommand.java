package com.dymbia.sumaqagro.platform.profiles.domain.model.commands;

import java.time.LocalDate;

/**
 * Command to add a member to a cooperative.
 *
 * @param cooperativeId identity of the cooperative
 * @param dni           DNI of the member (8 digits)
 * @param firstName     first name of the member
 * @param lastName      last name of the member
 * @param community     community or caserío of the member
 * @param joinedAt      date when the member joined the cooperative
 * @since 1.0
 */
public record AddCooperativeMemberCommand(
        Long cooperativeId,
        String dni,
        String firstName,
        String lastName,
        String community,
        LocalDate joinedAt,
        Long userId
) {
    public AddCooperativeMemberCommand {
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
    }
}