package com.dymbia.sumaqagro.platform.profiles.application.acl;

import com.dymbia.sumaqagro.platform.profiles.application.queryservices.ProfilesQueryService;
import com.dymbia.sumaqagro.platform.profiles.domain.model.aggregates.AgronomistAssignment;
import com.dymbia.sumaqagro.platform.profiles.domain.model.aggregates.Profile;
import com.dymbia.sumaqagro.platform.profiles.domain.model.queries.GetAgronomistAssignedToPlotQuery;
import com.dymbia.sumaqagro.platform.profiles.domain.model.queries.GetProfileByUserIdQuery;
import com.dymbia.sumaqagro.platform.profiles.domain.model.valueobjects.PersonName;
import com.dymbia.sumaqagro.platform.profiles.domain.model.valueobjects.PreferredLanguage;
import com.dymbia.sumaqagro.platform.profiles.domain.repositories.CooperativeRepository;
import com.dymbia.sumaqagro.platform.profiles.domain.repositories.ProfileRepository;
import com.dymbia.sumaqagro.platform.profiles.interfaces.acl.ProfilesContextFacade;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Application-layer implementation of the Profiles ACL facade.
 * Provides a simplified integration surface for other bounded contexts
 * without exposing internal Profiles models.
 *
 * @since 1.0
 */
@Service
public class ProfilesContextFacadeImpl implements ProfilesContextFacade {

    private final ProfilesQueryService profilesQueryService;
    private final CooperativeRepository cooperativeRepository;
    private final ProfileRepository profileRepository;

    public ProfilesContextFacadeImpl(ProfilesQueryService profilesQueryService,
                                     CooperativeRepository cooperativeRepository,
                                     ProfileRepository profileRepository) {
        this.profilesQueryService = profilesQueryService;
        this.cooperativeRepository = cooperativeRepository;
        this.profileRepository = profileRepository;
    }

    @Override
    public void createProfile(Long userId, String firstName, String lastName) {
        if (userId == null || userId <= 0) {
            return;
        }
        if (profileRepository.existsByUserId(userId)) {
            return;
        }

        PersonName name = new PersonName(firstName, lastName);
        Profile profile = new Profile(userId, name, null, null, PreferredLanguage.ES);
        profileRepository.save(profile);
    }

    @Override
    public Optional<String> fetchFullNameByUserId(Long userId) {
        return profilesQueryService.handle(new GetProfileByUserIdQuery(userId))
                .map(profile -> profile.getName().fullName());
    }

    @Override
    public Optional<Long> fetchAgronomistUserIdByPlotId(Long plotId) {
        if (plotId == null || plotId <= 0) {
            return Optional.empty();
        }

        return profilesQueryService.handle(new GetAgronomistAssignedToPlotQuery(plotId))
                .map(AgronomistAssignment::getAgronomistUserId);
    }

    @Override
    public boolean isMemberOfCooperative(Long cooperativeId, String dni) {
        if (cooperativeId == null || cooperativeId <= 0 || dni == null || dni.isBlank()) {
            return false;
        }

        return cooperativeRepository.findById(cooperativeId)
                .map(cooperative -> cooperative.getMembers().stream()
                        .anyMatch(member -> member.getDni().value().equals(dni.trim())))
                .orElse(false);
    }

    @Override
    public Optional<String> fetchPhoneNumberByUserId(Long userId) {
        if (userId == null || userId <= 0) {
            return Optional.empty();
        }

        return profilesQueryService.handle(new GetProfileByUserIdQuery(userId))
                .map(profile -> profile.getPhoneNumber() != null
                        ? profile.getPhoneNumber()
                        : null)
                .filter(phone -> phone != null && !phone.isBlank());
    }
}
