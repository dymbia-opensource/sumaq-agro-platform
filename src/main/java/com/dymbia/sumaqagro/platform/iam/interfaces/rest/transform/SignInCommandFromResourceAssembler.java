package com.dymbia.sumaqagro.platform.iam.interfaces.rest.transform;

import com.dymbia.sumaqagro.platform.iam.domain.model.commands.SignInCommand;
import com.dymbia.sumaqagro.platform.iam.domain.model.valueobjects.EmailAddress;
import com.dymbia.sumaqagro.platform.iam.interfaces.rest.resources.SignInResource;

/**
 * Converts the sign-in request into an IAM command.
 */
public final class SignInCommandFromResourceAssembler {

    private SignInCommandFromResourceAssembler() {
    }

    /**
     * Creates a sign-in command using the normalized account email.
     *
     * @param resource the validated sign-in request
     * @return the command consumed by the application service
     */
    public static SignInCommand toCommandFromResource(SignInResource resource) {
        return new SignInCommand(new EmailAddress(resource.email()), resource.password());
    }
}