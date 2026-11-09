package com.dymbia.sumaqagro.platform.profiles.interfaces.rest.resources;
/**
 * Response resource for a Cooperative.
 *
 * @since 1.0
 */
public record CooperativeResource(
        Long id,
        String ruc,
        String businessName,
        String region,
        Long managerUserId
) {
}