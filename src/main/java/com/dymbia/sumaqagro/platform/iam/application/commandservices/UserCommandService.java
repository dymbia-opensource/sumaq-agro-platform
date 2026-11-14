package com.dymbia.sumaqagro.platform.iam.application.commandservices;

import com.dymbia.sumaqagro.platform.iam.domain.model.aggregates.User;
import com.dymbia.sumaqagro.platform.iam.domain.model.commands.SignInCommand;
import com.dymbia.sumaqagro.platform.iam.domain.model.commands.SignUpCommand;
import com.dymbia.sumaqagro.platform.shared.application.result.ApplicationError;
import com.dymbia.sumaqagro.platform.shared.application.result.Result;
import org.apache.commons.lang3.tuple.ImmutablePair;

/**
 * Defines the application service contract for IAM user commands.
 */
public interface UserCommandService {

    /**
     * Registers a user with the requested role.
     *
     * @param command the registration data
     * @return the persisted user or an application error
     */
    Result<User, ApplicationError> handle(SignUpCommand command);

    /**
     * Authenticates a user and issues a signed access token.
     *
     * @param command the sign-in credentials
     * @return the authenticated user and token, or an application error
     */
    Result<ImmutablePair<User, String>, ApplicationError> handle(SignInCommand command);
}