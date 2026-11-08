package com.dymbia.sumaqagro.platform.profiles.interfaces.rest.resources;

/**
 * Request resource to update a profile.
 *
 * @since 1.0
 */
public record UpdateProfileResource(
        String firstName,
        String lastName,
        String phoneNumber,
        String photoUrl,
        String preferredLanguage
) {
}