package com.dymbia.sumaqagro.platform.profiles.infrastructure.persistence.jpa.adapters;

import com.dymbia.sumaqagro.platform.profiles.domain.model.aggregates.AgronomistAssignment;
import com.dymbia.sumaqagro.platform.profiles.domain.repositories.AgronomistAssignmentRepository;
import com.dymbia.sumaqagro.platform.profiles.infrastructure.persistence.jpa.assemblers.AgronomistAssignmentPersistenceAssembler;
import com.dymbia.sumaqagro.platform.profiles.infrastructure.persistence.jpa.entities.AgronomistAssignmentPersistenceEntity;
import com.dymbia.sumaqagro.platform.profiles.infrastructure.persistence.jpa.repositories.AgronomistAssignmentPersistenceRepository;
import com.dymbia.sumaqagro.platform.shared.infrastructure.events.DomainEventPublisher;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Adapter that implements the domain {@link AgronomistAssignmentRepository} with Spring Data JPA.
 *
 * @since 1.0
 */
@Repository
public class AgronomistAssignmentRepositoryImpl implements AgronomistAssignmentRepository {

    private final AgronomistAssignmentPersistenceRepository agronomistAssignmentPersistenceRepository;
    private final DomainEventPublisher domainEventPublisher;

    public AgronomistAssignmentRepositoryImpl(
            AgronomistAssignmentPersistenceRepository agronomistAssignmentPersistenceRepository,
            DomainEventPublisher domainEventPublisher) {
        this.agronomistAssignmentPersistenceRepository = agronomistAssignmentPersistenceRepository;
        this.domainEventPublisher = domainEventPublisher;
    }

    @Override
    public Optional<AgronomistAssignment> findById(Long id) {
        return agronomistAssignmentPersistenceRepository.findById(id)
                .map(AgronomistAssignmentPersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public Optional<AgronomistAssignment> findActiveByPlotId(Long plotId) {
        return agronomistAssignmentPersistenceRepository.findByPlotIdAndActiveTrue(plotId)
                .map(AgronomistAssignmentPersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public List<AgronomistAssignment> findAll() {
        return toDomain(agronomistAssignmentPersistenceRepository.findAll());
    }

    @Override
    public List<AgronomistAssignment> findAllByCooperativeId(Long cooperativeId) {
        return toDomain(agronomistAssignmentPersistenceRepository.findAllByCooperativeId(cooperativeId));
    }

    @Override
    public List<AgronomistAssignment> findAllByProducerUserId(Long producerUserId) {
        return toDomain(agronomistAssignmentPersistenceRepository.findAllByProducerUserId(producerUserId));
    }

    @Override
    public List<AgronomistAssignment> findAllByAgronomistUserId(Long agronomistUserId) {
        return toDomain(agronomistAssignmentPersistenceRepository.findAllByAgronomistUserId(agronomistUserId));
    }

    @Override
    public List<AgronomistAssignment> findAllByPlotId(Long plotId) {
        return toDomain(agronomistAssignmentPersistenceRepository.findAllByPlotId(plotId));
    }

    @Override
    public boolean existsActiveByPlotId(Long plotId) {
        return agronomistAssignmentPersistenceRepository.existsByPlotIdAndActiveTrue(plotId);
    }

    @Override
    public AgronomistAssignment save(AgronomistAssignment assignment) {
        boolean isNew = assignment.getId() == null;
        var savedEntity = agronomistAssignmentPersistenceRepository.save(
                AgronomistAssignmentPersistenceAssembler.toPersistenceFromDomain(assignment));
        var savedAssignment = AgronomistAssignmentPersistenceAssembler.toDomainFromPersistence(savedEntity);
        if (isNew) {
            savedAssignment.onAssigned();
            domainEventPublisher.publishAndClearEvents(savedAssignment);
        } else {
            domainEventPublisher.publishAndClearEvents(assignment);
        }
        return savedAssignment;
    }

    private static List<AgronomistAssignment> toDomain(List<AgronomistAssignmentPersistenceEntity> entities) {
        return entities.stream()
                .map(AgronomistAssignmentPersistenceAssembler::toDomainFromPersistence)
                .toList();
    }
}
