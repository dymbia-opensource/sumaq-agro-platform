package com.dymbia.sumaqagro.platform.profiles.application.queryservices;

import com.dymbia.sumaqagro.platform.profiles.domain.model.aggregates.AgronomistAssignment;
import com.dymbia.sumaqagro.platform.profiles.domain.model.aggregates.Cooperative;
import com.dymbia.sumaqagro.platform.profiles.domain.model.aggregates.Profile;
import com.dymbia.sumaqagro.platform.profiles.domain.model.queries.GetAgronomistAssignedToPlotQuery;
import com.dymbia.sumaqagro.platform.profiles.domain.model.queries.GetAgronomistAssignmentsByCooperativeQuery;
import com.dymbia.sumaqagro.platform.profiles.domain.model.queries.GetProfileByUserIdQuery;

import java.util.List;
import java.util.Optional;

/**
 * Query service for the Profiles bounded context.
 *
 * @since 1.0
 */
public interface ProfilesQueryService {

    /**
     * Retrieves a profile by user id.
     *
     * @param query the query
     * @return Optional containing the Profile if found
     */
    Optional<Profile> handle(GetProfileByUserIdQuery query);

    /**
     * Retrieves the active agronomist assignment for a plot.
     *
     * @param query the query
     * @return Optional containing the AgronomistAssignment if found
     */
    Optional<AgronomistAssignment> handle(GetAgronomistAssignedToPlotQuery query);

    /**
     * Retrieves all agronomist assignments of a cooperative.
     *
     * @param query the query
     * @return list of AgronomistAssignment
     */
    List<AgronomistAssignment> handle(GetAgronomistAssignmentsByCooperativeQuery query);
}