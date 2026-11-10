package com.dymbia.sumaqagro.platform.profiles.infrastructure.persistence.jpa.adapters;

import com.dymbia.sumaqagro.platform.profiles.domain.model.aggregates.AgronomistAssignment;
import com.dymbia.sumaqagro.platform.profiles.domain.repositories.AgronomistAssignmentRepository;
import com.dymbia.sumaqagro.platform.profiles.infrastructure.persistence.jpa.repositories.AgronomistAssignmentPersistenceRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Adapter that implements the AgronomistAssignment repository port with Spring Data JPA.
 *
 * @since 1.0
 */
@Repository
public class AgronomistAssignmentRepositoryImpl implements AgronomistAssignmentRepository {

    private final AgronomistAssignmentPersistenceRepository agronomistAssignmentPersistenceRepository;

    public AgronomistAssignmentRepositoryImpl(
            AgronomistAssignmentPersistenceRepository agronomistAssignmentPersistenceRepository) {
        this.agronomistAssignmentPersistenceRepository = agronomistAssignmentPersistenceRepository;
    }

    @Override
    public Optional<AgronomistAssignment> findById(Long id) {
        return agronomistAssignmentPersistenceRepository.findById(id);
    }

    @Override
    public Optional<AgronomistAssignment> findActiveByPlotId(Long plotId) {
        return agronomistAssignmentPersistenceRepository.findByPlotIdAndActiveTrue(plotId);
    }

    @Override
    public List<AgronomistAssignment> findAllByCooperativeId(Long cooperativeId) {
        return agronomistAssignmentPersistenceRepository.findAllByCooperativeId(cooperativeId);
    }

    @Override
    public boolean existsActiveByPlotId(Long plotId) {
        return agronomistAssignmentPersistenceRepository.existsByPlotIdAndActiveTrue(plotId);
    }

    @Override
    public AgronomistAssignment save(AgronomistAssignment assignment) {
        return agronomistAssignmentPersistenceRepository.save(assignment);
    }
}