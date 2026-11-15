package com.dymbia.sumaqagro.platform.profiles.domain.model.commands;

import com.dymbia.sumaqagro.platform.profiles.domain.model.valueobjects.PreferredLanguage;

/**
 * Command to update a user's profile.
 *
 * @param userId            identity of the user who owns the profile
 * @param firstName         new first name
 * @param lastName          new last name
 * @param phoneNumber       new phone number (optional)
 * @param photoUrl          new photo URL (optional)
 * @param preferredLanguage preferred language (ES or EN)
 * @since 1.0
 */
public record UpdateProfileCommand(
        Long profileId,
        String firstName,
        String lastName,
        String phoneNumber,
        String photoUrl,
        PreferredLanguage preferredLanguage
) {
    public UpdateProfileCommand {
        if (profileId == null || profileId <= 0) {
            throw new IllegalArgumentException("Profile id must be a positive number");
        }
        if (firstName == null || firstName.isBlank()) {
            throw new IllegalArgumentException("First name must not be null or blank");
        }
        if (lastName == null || lastName.isBlank()) {
            throw new IllegalArgumentException("Last name must not be null or blank");
        }
    }
}