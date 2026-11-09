package com.dymbia.sumaqagro.platform.profiles.interfaces.rest.resources;
import java.time.Instant;

/**
 * Response resource for an Agronomist Invitation.
 *
 * @since 1.0
 */
public record AgronomistInvitationResource(
        Long id,
        Long cooperativeId,
        String email,
        String status,
        Instant sentAt
) {
}
