package com.dymbia.sumaqagro.platform.profiles.application.internal.commandservices;

import com.dymbia.sumaqagro.platform.profiles.application.commandservices.ProfileCommandService;
import com.dymbia.sumaqagro.platform.profiles.domain.model.aggregates.Profile;
import com.dymbia.sumaqagro.platform.profiles.domain.model.commands.CreateProfileCommand;
import com.dymbia.sumaqagro.platform.profiles.domain.model.commands.UpdateProfileCommand;
import com.dymbia.sumaqagro.platform.profiles.domain.repositories.ProfileRepository;
import com.dymbia.sumaqagro.platform.shared.application.result.ApplicationError;
import com.dymbia.sumaqagro.platform.shared.application.result.Result;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProfileCommandServiceImpl implements ProfileCommandService {

    private final ProfileRepository profileRepository;

    public ProfileCommandServiceImpl(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    // inherited javadoc
    @Override
    @Transactional
    public Result<Profile, ApplicationError> handle(CreateProfileCommand command) {
        try {
            if (profileRepository.existsByUserId(command.userId())) {
                return Result.failure(ApplicationError.conflict(
                        "Profile", "A profile for user '%s' already exists".formatted(command.userId())));
            }
            var profile = new Profile(command);
            return Result.success(profileRepository.save(profile));
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.validationError("Profile", e.getMessage()));
        } catch (Exception e) {
            return Result.failure(ApplicationError.unexpected("Profile creation", e.getMessage()));
        }
    }

    // inherited javadoc
    @Override
    @Transactional
    public Result<Profile, ApplicationError> handle(UpdateProfileCommand command) {
        try {
            var profileOptional = profileRepository.findById(command.profileId());
            if (profileOptional.isEmpty()) {
                return Result.failure(ApplicationError.notFound("Profile", command.profileId().toString()));
            }
            var profile = profileOptional.get();
            profile.update(command);
            return Result.success(profileRepository.save(profile));
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.validationError("Profile", e.getMessage()));
        } catch (Exception e) {
            return Result.failure(ApplicationError.unexpected("Profile update", e.getMessage()));
        }
    }
}
