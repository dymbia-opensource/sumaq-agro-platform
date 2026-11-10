package com.dymbia.sumaqagro.platform.profiles.application.acl;

import com.dymbia.sumaqagro.platform.profiles.application.queryservices.ProfilesQueryService;
import com.dymbia.sumaqagro.platform.profiles.domain.model.queries.GetAgronomistAssignedToPlotQuery;
import com.dymbia.sumaqagro.platform.profiles.domain.model.queries.GetProfileByUserIdQuery;
import com.dymbia.sumaqagro.platform.profiles.domain.repositories.CooperativeRepository;
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

    public ProfilesContextFacadeImpl(ProfilesQueryService profilesQueryService,
                                     CooperativeRepository cooperativeRepository) {
        this.profilesQueryService = profilesQueryService;
        this.cooperativeRepository = cooperativeRepository;
    }

    @Override
    public Optional<Long> fetchAgronomistUserIdByPlotId(Long plotId) {
        if (plotId == null || plotId <= 0) {
            return Optional.empty();
        }

        return profilesQueryService.handle(new GetAgronomistAssignedToPlotQuery(plotId))
                .map(assignment -> assignment.getAgronomistUserId());
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
                        ? profile.getPhoneNumber().value()
                        : null)
                .filter(phone -> phone != null && !phone.isBlank());
    }
}