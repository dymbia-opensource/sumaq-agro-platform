package com.dymbia.sumaqagro.platform.profiles.infrastructure.persistence.jpa.adapters;

import com.dymbia.sumaqagro.platform.profiles.domain.model.aggregates.Cooperative;
import com.dymbia.sumaqagro.platform.profiles.domain.repositories.CooperativeRepository;
import com.dymbia.sumaqagro.platform.profiles.infrastructure.persistence.jpa.assemblers.CooperativePersistenceAssembler;
import com.dymbia.sumaqagro.platform.profiles.infrastructure.persistence.jpa.repositories.CooperativePersistenceRepository;
import com.dymbia.sumaqagro.platform.shared.infrastructure.events.DomainEventPublisher;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Adapter that implements the domain {@link CooperativeRepository} with Spring Data JPA.
 *
 * @since 1.0
 */
@Repository
public class CooperativeRepositoryImpl implements CooperativeRepository {

    private final CooperativePersistenceRepository cooperativePersistenceRepository;
    private final DomainEventPublisher domainEventPublisher;

    public CooperativeRepositoryImpl(CooperativePersistenceRepository cooperativePersistenceRepository,
                                     DomainEventPublisher domainEventPublisher) {
        this.cooperativePersistenceRepository = cooperativePersistenceRepository;
        this.domainEventPublisher = domainEventPublisher;
    }

    @Override
    public Optional<Cooperative> findById(Long id) {
        return cooperativePersistenceRepository.findById(id)
                .map(CooperativePersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public Optional<Cooperative> findByRuc(String ruc) {
        return cooperativePersistenceRepository.findByRuc(ruc)
                .map(CooperativePersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public List<Cooperative> findAll() {
        return cooperativePersistenceRepository.findAll().stream()
                .map(CooperativePersistenceAssembler::toDomainFromPersistence)
                .toList();
    }

    @Override
    public List<Cooperative> findAllByManagerUserId(Long managerUserId) {
        return cooperativePersistenceRepository.findAllByManagerUserId(managerUserId).stream()
                .map(CooperativePersistenceAssembler::toDomainFromPersistence)
                .toList();
    }

    @Override
    public List<Cooperative> findAllByMemberUserId(Long userId) {
        return cooperativePersistenceRepository.findAllByMemberUserId(userId).stream()
                .map(CooperativePersistenceAssembler::toDomainFromPersistence)
                .toList();
    }

    @Override
    public boolean existsById(Long id) {
        return cooperativePersistenceRepository.existsById(id);
    }

    @Override
    public boolean existsByRuc(String ruc) {
        return cooperativePersistenceRepository.existsByRuc(ruc);
    }

    @Override
    public boolean existsByRucAndIdIsNot(String ruc, Long id) {
        return cooperativePersistenceRepository.existsByRucAndIdIsNot(ruc, id);
    }

    @Override
    public Cooperative save(Cooperative cooperative) {
        boolean isNew = cooperative.getId() == null;
        var savedEntity = cooperativePersistenceRepository.save(
                CooperativePersistenceAssembler.toPersistenceFromDomain(cooperative));
        var savedCooperative = CooperativePersistenceAssembler.toDomainFromPersistence(savedEntity);
        if (isNew) {
            // Same idea as the professor's Profile.onCreated(): the event is created when the id exists.
            savedCooperative.onRegistered();
            domainEventPublisher.publishAndClearEvents(savedCooperative);
        } else {
            // Events from addMember() / inviteAgronomist() live on the object we received.
            domainEventPublisher.publishAndClearEvents(cooperative);
        }
        return savedCooperative;
    }
}
