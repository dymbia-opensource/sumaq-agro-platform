package com.dymbia.sumaqagro.platform.iam.interfaces.rest.transform;

import com.dymbia.sumaqagro.platform.iam.domain.model.commands.SignUpCommand;
import com.dymbia.sumaqagro.platform.iam.domain.model.valueobjects.EmailAddress;
import com.dymbia.sumaqagro.platform.iam.domain.model.valueobjects.Roles;
import com.dymbia.sumaqagro.platform.iam.interfaces.rest.resources.SignUpResource;

/**
 * Converts a registration resource into an IAM command.
 */
public final class SignUpCommandFromResourceAssembler {

    private SignUpCommandFromResourceAssembler() {
    }

    /**
     * Creates a sign-up command from a validated request.
     *
     * @param resource the validated registration resource
     * @return the command consumed by the IAM application service
     */
    public static SignUpCommand toCommandFromResource(SignUpResource resource) {
        return new SignUpCommand(
                new EmailAddress(resource.email()),
                resource.password(),
                Roles.valueOf(resource.roles().getFirst()),
                resource.profile().firstName(),
                resource.profile().lastName()
        );
    }
}