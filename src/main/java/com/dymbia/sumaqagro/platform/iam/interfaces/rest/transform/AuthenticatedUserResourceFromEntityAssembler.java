package com.dymbia.sumaqagro.platform.iam.interfaces.rest.transform;

import com.dymbia.sumaqagro.platform.iam.domain.model.aggregates.User;
import com.dymbia.sumaqagro.platform.iam.domain.model.entities.Role;
import com.dymbia.sumaqagro.platform.iam.interfaces.rest.resources.AuthenticatedUserResource;

/**
 * Converts an authenticated user and token into a REST response resource.
 */
public final class AuthenticatedUserResourceFromEntityAssembler {

    private AuthenticatedUserResourceFromEntityAssembler() {
    }

    /**
     * Creates the public authentication response.
     *
     * @param user the authenticated user
     * @param token the signed access token
     * @return the response resource with the account roles
     */
    public static AuthenticatedUserResource toResourceFromEntity(User user, String token) {
        var roles = user.getRoles().stream()
                .map(Role::getStringName)
                .sorted()
                .toList();

        return new AuthenticatedUserResource(
                user.getId(),
                user.getEmail().value(),
                roles,
                token
        );
    }
}