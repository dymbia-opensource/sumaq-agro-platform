package com.dymbia.sumaqagro.platform.profiles.application.commandservices;

import com.dymbia.sumaqagro.platform.profiles.domain.model.aggregates.Profile;
import com.dymbia.sumaqagro.platform.profiles.domain.model.commands.CreateProfileCommand;
import com.dymbia.sumaqagro.platform.profiles.domain.model.commands.UpdateProfileCommand;
import com.dymbia.sumaqagro.platform.shared.application.result.ApplicationError;
import com.dymbia.sumaqagro.platform.shared.application.result.Result;

/**
 * Write operations of the {@link Profile} aggregate.
 *
 * @since 1.0
 */
public interface ProfileCommandService {
    Result<Profile, ApplicationError> handle(CreateProfileCommand command);

    Result<Profile, ApplicationError> handle(UpdateProfileCommand command);
}
