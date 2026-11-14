package com.dymbia.sumaqagro.platform.profiles.application.acl;

import com.dymbia.sumaqagro.platform.profiles.application.commandservices.ProfileCommandService;
import com.dymbia.sumaqagro.platform.profiles.application.queryservices.AgronomistAssignmentQueryService;
import com.dymbia.sumaqagro.platform.profiles.application.queryservices.CooperativeQueryService;
import com.dymbia.sumaqagro.platform.profiles.application.queryservices.ProfileQueryService;
import com.dymbia.sumaqagro.platform.profiles.domain.model.aggregates.AgronomistAssignment;
import com.dymbia.sumaqagro.platform.profiles.domain.model.aggregates.Profile;
import com.dymbia.sumaqagro.platform.profiles.domain.model.commands.CreateProfileCommand;
import com.dymbia.sumaqagro.platform.profiles.domain.model.queries.GetAgronomistAssignedToPlotQuery;
import com.dymbia.sumaqagro.platform.profiles.domain.model.queries.GetCooperativeByIdQuery;
import com.dymbia.sumaqagro.platform.profiles.domain.model.queries.GetProfileByUserIdQuery;
import com.dymbia.sumaqagro.platform.profiles.interfaces.acl.ProfilesContextFacade;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Implementation of {@link ProfilesContextFacade}. Like the professor's facade, it only talks
 * to command and query services, never to repositories.
 *
 * @since 1.0
 */
@Service
public class ProfilesContextFacadeImpl implements ProfilesContextFacade {

    private final ProfileCommandService profileCommandService;
    private final ProfileQueryService profileQueryService;
    private final CooperativeQueryService cooperativeQueryService;
    private final AgronomistAssignmentQueryService agronomistAssignmentQueryService;

    public ProfilesContextFacadeImpl(ProfileCommandService profileCommandService,
                                     ProfileQueryService profileQueryService,
                                     CooperativeQueryService cooperativeQueryService,
                                     AgronomistAssignmentQueryService agronomistAssignmentQueryService) {
        this.profileCommandService = profileCommandService;
        this.profileQueryService = profileQueryService;
        this.cooperativeQueryService = cooperativeQueryService;
        this.agronomistAssignmentQueryService = agronomistAssignmentQueryService;
    }

    @Override
    public Long createProfile(Long userId, String firstName, String lastName) {
        try {
            var existing = profileQueryService.handle(new GetProfileByUserIdQuery(userId));
            if (existing.isPresent()) {
                return existing.get().getId();
            }
            var result = profileCommandService.handle(new CreateProfileCommand(userId, firstName, lastName));
            return result.toOptional().map(Profile::getId).orElse(0L);
        } catch (IllegalArgumentException e) {
            return 0L;
        }
    }

    @Override
    public Optional<Long> fetchAgronomistUserIdByPlotId(Long plotId) {
        if (plotId == null || plotId <= 0) return Optional.empty();
        return agronomistAssignmentQueryService.handle(new GetAgronomistAssignedToPlotQuery(plotId))
                .map(AgronomistAssignment::getAgronomistUserId);
    }

    @Override
    public boolean isMemberOfCooperative(Long cooperativeId, String dni) {
        if (cooperativeId == null || cooperativeId <= 0 || dni == null || dni.isBlank()) return false;
        return cooperativeQueryService.handle(new GetCooperativeByIdQuery(cooperativeId))
                .map(cooperative -> cooperative.hasActiveMemberWithDni(dni))
                .orElse(false);
    }

    @Override
    public Optional<String> fetchPhoneNumberByUserId(Long userId) {
        if (userId == null || userId <= 0) return Optional.empty();
        return profileQueryService.handle(new GetProfileByUserIdQuery(userId))
                .map(Profile::getPhoneNumber)
                .filter(phone -> !phone.isBlank());
    }
}
