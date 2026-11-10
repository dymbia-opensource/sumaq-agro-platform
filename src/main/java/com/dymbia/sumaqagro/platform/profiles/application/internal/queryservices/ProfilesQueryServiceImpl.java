package com.dymbia.sumaqagro.platform.profiles.application.internal.queryservices;

import com.dymbia.sumaqagro.platform.profiles.application.queryservices.ProfilesQueryService;
import com.dymbia.sumaqagro.platform.profiles.domain.model.aggregates.AgronomistAssignment;
import com.dymbia.sumaqagro.platform.profiles.domain.model.aggregates.Profile;
import com.dymbia.sumaqagro.platform.profiles.domain.model.queries.GetAgronomistAssignedToPlotQuery;
import com.dymbia.sumaqagro.platform.profiles.domain.model.queries.GetAgronomistAssignmentsByCooperativeQuery;
import com.dymbia.sumaqagro.platform.profiles.domain.model.queries.GetProfileByUserIdQuery;
import com.dymbia.sumaqagro.platform.profiles.infrastructure.persistence.jpa.repositories.AgronomistAssignmentPersistenceRepository;
import com.dymbia.sumaqagro.platform.profiles.infrastructure.persistence.jpa.repositories.ProfilePersistenceRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProfilesQueryServiceImpl implements ProfilesQueryService {

    private final ProfilePersistenceRepository profilePersistenceRepository;
    private final AgronomistAssignmentPersistenceRepository agronomistAssignmentPersistenceRepository;

    public ProfilesQueryServiceImpl(ProfilePersistenceRepository profilePersistenceRepository,
                                    AgronomistAssignmentPersistenceRepository agronomistAssignmentPersistenceRepository) {
        this.profilePersistenceRepository = profilePersistenceRepository;
        this.agronomistAssignmentPersistenceRepository = agronomistAssignmentPersistenceRepository;
    }

    @Override
    public Optional<Profile> handle(GetProfileByUserIdQuery query) {
        return profilePersistenceRepository.findByUserId(query.userId());
    }

    @Override
    public Optional<AgronomistAssignment> handle(GetAgronomistAssignedToPlotQuery query) {
        return agronomistAssignmentPersistenceRepository.findByPlotIdAndActiveTrue(query.plotId());
    }

    @Override
    public List<AgronomistAssignment> handle(GetAgronomistAssignmentsByCooperativeQuery query) {
        return agronomistAssignmentPersistenceRepository.findAllByCooperativeId(query.cooperativeId());
    }
}