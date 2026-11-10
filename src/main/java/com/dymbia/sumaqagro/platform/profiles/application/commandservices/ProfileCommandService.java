package com.dymbia.sumaqagro.platform.profiles.application.commandservices;

import com.dymbia.sumaqagro.platform.profiles.domain.model.aggregates.Profile;
import com.dymbia.sumaqagro.platform.profiles.domain.model.commands.UpdateProfileCommand;
import com.dymbia.sumaqagro.platform.shared.application.result.ApplicationError;
import com.dymbia.sumaqagro.platform.shared.application.result.Result;

/**
 * Command service for the Profile aggregate.
 *
 * @since 1.0
 */
public interface ProfileCommandService {

    /**
     * Handles the update of a user profile.
     *
     * @param command the update command
     * @return Result containing the updated Profile or an ApplicationError
     */
    Result<Profile, ApplicationError> handle(UpdateProfileCommand command);
}