package com.dymbia.sumaqagro.platform.crop.application.internal.outboundservices.acl;

import com.dymbia.sumaqagro.platform.profiles.interfaces.acl.ProfilesContextFacade;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Anti-corruption layer of Crop towards Profiles. It only uses the Profiles public facade.
 * <p>
 * The bean has its own name so it never clashes with another context's {@code ExternalProfilesService}.
 * </p>
 *
 * @since 1.1
 */
@Service("cropExternalProfilesService")
public class ExternalProfilesService {

    private final ProfilesContextFacade profilesContextFacade;

    public ExternalProfilesService(ProfilesContextFacade profilesContextFacade) {
        this.profilesContextFacade = profilesContextFacade;
    }

    /**
     * Finds the contact phone of a producer.
     *
     * @param userId the producer user id
     * @return the phone of the profile, or empty when the producer has none
     */
    public Optional<String> fetchPhoneNumberByUserId(Long userId) {
        var phone = profilesContextFacade.fetchPhoneNumberByUserId(userId);
        return phone == null ? Optional.empty() : phone.filter(value -> !value.isBlank());
    }
}
