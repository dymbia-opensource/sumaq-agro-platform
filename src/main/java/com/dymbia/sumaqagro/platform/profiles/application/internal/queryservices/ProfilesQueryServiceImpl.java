package com.dymbia.sumaqagro.platform.profiles.application.internal.queryservices;

import com.dymbia.sumaqagro.platform.profiles.application.queryservices.ProfilesQueryService;
import com.dymbia.sumaqagro.platform.profiles.domain.model.aggregates.AgronomistAssignment;
import com.dymbia.sumaqagro.platform.profiles.domain.model.aggregates.Profile;
import com.dymbia.sumaqagro.platform.profiles.domain.model.queries.GetAgronomistAssignedToPlotQuery;
import com.dymbia.sumaqagro.platform.profiles.domain.model.queries.GetAgronomistAssignmentsByCooperativeQuery;
import com.dymbia.sumaqagro.platform.profiles.domain.model.queries.GetProfileByUserIdQuery;
import com.dymbia.sumaqagro.platform.profiles.domain.repositories.AgronomistAssignmentRepository;
import com.dymbia.sumaqagro.platform.profiles.domain.repositories.ProfileRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of ProfilesQueryService.
 *
 * @since 1.0
 */
@Service
public class ProfilesQueryServiceImpl implements ProfilesQueryService {

    private final ProfileRepository profileRepository;
    private final AgronomistAssignmentRepository agronomistAssignmentRepository;

    public ProfilesQueryServiceImpl(ProfileRepository profileRepository,
                                    AgronomistAssignmentRepository agronomistAssignmentRepository) {
        this.profileRepository = profileRepository;
        this.agronomistAssignmentRepository = agronomistAssignmentRepository;
    }

    @Override
    public Optional<Profile> handle(GetProfileByUserIdQuery query) {
        return profileRepository.findByUserId(query.userId());
    }

    @Override
    public Optional<AgronomistAssignment> handle(GetAgronomistAssignedToPlotQuery query) {
        return agronomistAssignmentRepository.findActiveByPlotId(query.plotId());
    }

    @Override
    public List<AgronomistAssignment> handle(GetAgronomistAssignmentsByCooperativeQuery query) {
        return agronomistAssignmentRepository.findAllByCooperativeId(query.cooperativeId());
    }
}