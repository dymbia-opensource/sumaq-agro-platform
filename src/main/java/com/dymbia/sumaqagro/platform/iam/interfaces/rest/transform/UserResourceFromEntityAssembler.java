package com.dymbia.sumaqagro.platform.iam.interfaces.rest.transform;

import com.dymbia.sumaqagro.platform.iam.domain.model.aggregates.User;
import com.dymbia.sumaqagro.platform.iam.domain.model.entities.Role;
import com.dymbia.sumaqagro.platform.iam.interfaces.rest.resources.UserResource;

/**
 * Converts the IAM user aggregate into a public REST resource.
 */
public final class UserResourceFromEntityAssembler {

    private UserResourceFromEntityAssembler() {
    }

    /**
     * Maps the account fields returned by the registration endpoint.
     *
     * @param user the persisted user aggregate
     * @return the public account resource
     */
    public static UserResource toResourceFromEntity(User user) {
        var roles = user.getRoles().stream()
                .map(Role::getStringName)
                .sorted()
                .toList();

        return new UserResource(user.getId(), user.getEmail().value(), roles);
    }
}