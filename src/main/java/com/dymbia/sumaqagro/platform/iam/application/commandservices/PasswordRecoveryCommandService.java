package com.dymbia.sumaqagro.platform.iam.application.commandservices;

import com.dymbia.sumaqagro.platform.iam.domain.model.commands.RequestPasswordResetCommand;
import com.dymbia.sumaqagro.platform.iam.domain.model.commands.ResetPasswordCommand;
import com.dymbia.sumaqagro.platform.shared.application.result.ApplicationError;
import com.dymbia.sumaqagro.platform.shared.application.result.Result;

/** Exposes the IAM password recovery use cases. */
public interface PasswordRecoveryCommandService {
    /** Requests delivery of a recovery link. */
    void handle(RequestPasswordResetCommand command);

    /** Restores account access using a valid, unused recovery token. */
    Result<Long, ApplicationError> handle(ResetPasswordCommand command);
}
