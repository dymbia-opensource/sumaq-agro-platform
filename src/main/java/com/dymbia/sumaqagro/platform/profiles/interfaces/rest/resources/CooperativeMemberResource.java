package com.dymbia.sumaqagro.platform.profiles.interfaces.rest.resources;

import java.time.LocalDate;

/**
 * Response resource for a Cooperative Member.
 *
 * @since 1.0
 */
public record CooperativeMemberResource(
        Long id,
        Long cooperativeId,
        String dni,
        String firstName,
        String lastName,
        String community,
        LocalDate joinedAt
) {
}