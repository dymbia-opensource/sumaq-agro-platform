package com.dymbia.sumaqagro.platform.profiles.interfaces.acl;

import com.dymbia.sumaqagro.platform.profiles.domain.model.aggregates.Profile;
import com.dymbia.sumaqagro.platform.profiles.domain.model.valueobjects.PersonName;
import com.dymbia.sumaqagro.platform.profiles.domain.model.valueobjects.PreferredLanguage;
import com.dymbia.sumaqagro.platform.profiles.domain.repositories.ProfileRepository;
import org.springframework.stereotype.Component;

/**
 * Exposes profile creation to other bounded contexts.
 */
@Component
public class ProfilesContextFacade {

    private final ProfileRepository profileRepository;

    public ProfilesContextFacade(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    /**
     * Creates the initial profile for a newly registered user.
     *
     * @param userId the identifier assigned by IAM
     * @param firstName the user's given names
     * @param lastName the user's family names
     */
    public void createProfile(Long userId, String firstName, String lastName) {
        var name = new PersonName(firstName, lastName);
        var profile = new Profile(userId, name, null, null, PreferredLanguage.ES);
        profileRepository.save(profile);
    }
}