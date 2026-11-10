package com.dymbia.sumaqagro.platform.profiles.application.internal.commandservices;

import com.dymbia.sumaqagro.platform.profiles.application.commandservices.ProfileCommandService;
import com.dymbia.sumaqagro.platform.profiles.domain.model.aggregates.Profile;
import com.dymbia.sumaqagro.platform.profiles.domain.model.commands.UpdateProfileCommand;
import com.dymbia.sumaqagro.platform.profiles.infrastructure.persistence.jpa.repositories.ProfilePersistenceRepository;
import com.dymbia.sumaqagro.platform.shared.application.result.ApplicationError;
import com.dymbia.sumaqagro.platform.shared.application.result.Result;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProfileCommandServiceImpl implements ProfileCommandService {

    private final ProfilePersistenceRepository profilePersistenceRepository;

    public ProfileCommandServiceImpl(ProfilePersistenceRepository profilePersistenceRepository) {
        this.profilePersistenceRepository = profilePersistenceRepository;
    }

    @Override
    @Transactional
    public Result<Profile, ApplicationError> handle(UpdateProfileCommand command) {
        try {
            var profileOptional = profilePersistenceRepository.findByUserId(command.userId());

            if (profileOptional.isEmpty()) {
                return Result.failure(ApplicationError.notFound("Profile", "userId=" + command.userId()));
            }

            Profile profile = profileOptional.get();
            profile.update(command);
            Profile updatedProfile = profilePersistenceRepository.save(profile);

            return Result.success(updatedProfile);
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.validationError("Profile", e.getMessage()));
        } catch (Exception e) {
            return Result.failure(ApplicationError.unexpected("Profile update", e.getMessage()));
        }
    }
}