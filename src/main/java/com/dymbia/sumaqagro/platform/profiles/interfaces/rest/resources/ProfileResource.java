package com.dymbia.sumaqagro.platform.profiles.interfaces.rest.resources;
/**
 * Response resource for a Profile.
 *
 * @since 1.0
 */
public record ProfileResource(
        Long id,
        Long userId,
        String firstName,
        String lastName,
        String phoneNumber,
        String photoUrl,
        String preferredLanguage
) {
}