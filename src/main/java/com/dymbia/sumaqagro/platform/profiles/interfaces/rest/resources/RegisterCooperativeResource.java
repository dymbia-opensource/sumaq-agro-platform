package com.dymbia.sumaqagro.platform.profiles.interfaces.rest.resources;

/**
 * Request resource to register a cooperative.
 *
 * @since 1.0
 */
public record RegisterCooperativeResource(
        String ruc,
        String businessName,
        String region,
        Long managerUserId
) {
}
