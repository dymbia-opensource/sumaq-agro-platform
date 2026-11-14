package com.dymbia.sumaqagro.platform.profiles.domain.model.commands;

/**
 * Command to create the profile of a user that has just signed up in IAM.
 *
 * @since 1.0
 */
public record CreateProfileCommand(Long userId, String firstName, String lastName) {
    public CreateProfileCommand {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("User id must be a positive number");
        }
        if (firstName == null || firstName.isBlank()) {
            throw new IllegalArgumentException("First name must not be null or blank");
        }
        if (lastName == null || lastName.isBlank()) {
            throw new IllegalArgumentException("Last name must not be null or blank");
        }
    }
}
