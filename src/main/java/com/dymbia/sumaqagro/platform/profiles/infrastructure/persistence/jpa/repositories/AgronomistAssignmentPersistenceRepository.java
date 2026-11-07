package com.dymbia.sumaqagro.platform.profiles.infrastructure.persistence.jpa.repositories;

import com.dymbia.sumaqagro.platform.profiles.domain.model.aggregates.AgronomistAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for the AgronomistAssignment aggregate.
 *
 * @since 1.0
 */
@Repository
public interface AgronomistAssignmentPersistenceRepository extends JpaRepository<AgronomistAssignment, Long> {

    Optional<AgronomistAssignment> findByPlotIdAndActiveTrue(Long plotId);

    List<AgronomistAssignment> findAllByCooperativeId(Long cooperativeId);

    boolean existsByPlotIdAndActiveTrue(Long plotId);
}