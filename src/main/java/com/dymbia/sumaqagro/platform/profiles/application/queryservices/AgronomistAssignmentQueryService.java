package com.dymbia.sumaqagro.platform.profiles.application.queryservices;

import com.dymbia.sumaqagro.platform.profiles.domain.model.aggregates.AgronomistAssignment;
import com.dymbia.sumaqagro.platform.profiles.domain.model.queries.*;

import java.util.List;
import java.util.Optional;

/**
 * Read operations of the {@link AgronomistAssignment} aggregate.
 *
 * @since 1.0
 */
public interface AgronomistAssignmentQueryService {
    Optional<AgronomistAssignment> handle(GetAgronomistAssignmentByIdQuery query);

    Optional<AgronomistAssignment> handle(GetAgronomistAssignedToPlotQuery query);

    List<AgronomistAssignment> handle(GetAllAgronomistAssignmentsQuery query);

    List<AgronomistAssignment> handle(GetAgronomistAssignmentsByCooperativeQuery query);

    List<AgronomistAssignment> handle(GetAgronomistAssignmentsByProducerUserIdQuery query);

    List<AgronomistAssignment> handle(GetAgronomistAssignmentsByAgronomistUserIdQuery query);

    List<AgronomistAssignment> handle(GetAgronomistAssignmentsByPlotIdQuery query);
}
