package com.dymbia.sumaqagro.platform.profiles.interfaces.rest;

import com.dymbia.sumaqagro.platform.iam.infrastructure.authorization.sfs.model.UserDetailsImpl;
import com.dymbia.sumaqagro.platform.profiles.application.queryservices.AgronomistAssignmentQueryService;
import com.dymbia.sumaqagro.platform.profiles.application.queryservices.CooperativeQueryService;
import com.dymbia.sumaqagro.platform.profiles.domain.model.aggregates.AgronomistAssignment;
import com.dymbia.sumaqagro.platform.profiles.domain.model.queries.GetAgronomistAssignmentByIdQuery;
import com.dymbia.sumaqagro.platform.profiles.domain.model.queries.GetCooperativeByIdQuery;
import org.springframework.stereotype.Component;

/**
 * Access rules of the cooperative endpoints.
 * <p>
 * The role is checked with {@code @PreAuthorize}; this class checks the data: a director only
 * manages his own cooperative and the assignments of its plots. It reads through the query
 * services, never through repositories.
 * </p>
 *
 * @since 1.2
 */
@Component
public class CooperativeAccess {
    private final CooperativeQueryService cooperatives;
    private final AgronomistAssignmentQueryService assignments;

    public CooperativeAccess(CooperativeQueryService cooperatives, AgronomistAssignmentQueryService assignments) {
        this.cooperatives = cooperatives;
        this.assignments = assignments;
    }

    /**
     * Whether the authenticated user is the director of the cooperative.
     *
     * @param principal      authenticated user
     * @param cooperativeId  cooperative of the request
     */
    boolean manages(UserDetailsImpl principal, Long cooperativeId) {
        return principal != null && cooperativeId != null
                && cooperatives.handle(new GetCooperativeByIdQuery(cooperativeId))
                .map(cooperative -> cooperative.getManagerUserId().equals(principal.getId()))
                .orElse(false);
    }

    /**
     * Whether the authenticated user directs the cooperative of an assignment.
     *
     * @param principal    authenticated user
     * @param assignmentId assignment of the request
     */
    boolean managesAssignment(UserDetailsImpl principal, Long assignmentId) {
        return assignmentId != null
                && assignments.handle(new GetAgronomistAssignmentByIdQuery(assignmentId))
                .map(AgronomistAssignment::getCooperativeId)
                .map(cooperativeId -> manages(principal, cooperativeId))
                .orElse(false);
    }
}
