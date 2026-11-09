package com.dymbia.sumaqagro.platform.profiles.interfaces.rest.resources;
import java.time.Instant;

/**
 * Response resource for an Agronomist Assignment.
 *
 * @since 1.0
 */
public record AgronomistAssignmentResource(
        Long id,
        Long cooperativeId,
        Long agronomistUserId,
        Long plotId,
        Instant assignedAt,
        boolean active
) {
}