package com.dymbia.sumaqagro.platform.profiles.infrastructure.persistence.jpa.adapters;

import com.dymbia.sumaqagro.platform.profiles.domain.model.aggregates.Cooperative;
import com.dymbia.sumaqagro.platform.profiles.domain.repositories.CooperativeRepository;
import com.dymbia.sumaqagro.platform.profiles.infrastructure.persistence.jpa.assemblers.CooperativePersistenceAssembler;
import com.dymbia.sumaqagro.platform.profiles.infrastructure.persistence.jpa.repositories.CooperativePersistenceRepository;
import com.dymbia.sumaqagro.platform.shared.infrastructure.events.DomainEventPublisher;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository adapter that bridges the domain cooperative repository port with Spring Data JPA.
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
    public boolean existsByRuc(String ruc) {
        return cooperativePersistenceRepository.existsByRuc(ruc);
    }

    @Override
    public Cooperative save(Cooperative cooperative) {
        var savedEntity = cooperativePersistenceRepository.save(
                CooperativePersistenceAssembler.toPersistenceFromDomain(cooperative));
        var savedCooperative = CooperativePersistenceAssembler.toDomainFromPersistence(savedEntity);

        domainEventPublisher.publishAndClearEvents(savedCooperative);
        return savedCooperative;
    }
}