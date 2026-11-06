package com.dymbia.sumaqagro.platform.iam.application.commandservices;

import com.dymbia.sumaqagro.platform.iam.domain.model.aggregates.User;
import com.dymbia.sumaqagro.platform.iam.domain.model.commands.SignUpCommand;
import com.dymbia.sumaqagro.platform.shared.application.result.ApplicationError;
import com.dymbia.sumaqagro.platform.shared.application.result.Result;

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
}