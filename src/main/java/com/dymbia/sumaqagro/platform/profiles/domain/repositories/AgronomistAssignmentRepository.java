package com.dymbia.sumaqagro.platform.profiles.domain.repositories;

import com.dymbia.sumaqagro.platform.profiles.domain.model.aggregates.AgronomistAssignment;

import java.util.List;
import java.util.Optional;

/**
 * Domain repository for the AgronomistAssignment aggregate.
 *
 * @since 1.0
 */
public interface AgronomistAssignmentRepository {

    AgronomistAssignment save(AgronomistAssignment assignment);

    Optional<AgronomistAssignment> findById(Long id);

    Optional<AgronomistAssignment> findActiveByPlotId(Long plotId);

    List<AgronomistAssignment> findAllByCooperativeId(Long cooperativeId);

    boolean existsActiveByPlotId(Long plotId);
}