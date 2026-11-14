package com.dymbia.sumaqagro.platform.crop.infrastructure.persistence.jpa.adapters;

import com.dymbia.sumaqagro.platform.crop.domain.model.aggregates.AgroclimaticAlert;
import com.dymbia.sumaqagro.platform.crop.domain.repositories.AgroclimaticAlertRepository;
import com.dymbia.sumaqagro.platform.crop.infrastructure.persistence.jpa.assemblers.AgroclimaticAlertPersistenceAssembler;
import com.dymbia.sumaqagro.platform.crop.infrastructure.persistence.jpa.repositories.AgroclimaticAlertPersistenceRepository;
import com.dymbia.sumaqagro.platform.shared.infrastructure.events.DomainEventPublisher;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Implements the agroclimatic alert domain repository using Spring Data JPA.
 */
@Repository
public class AgroclimaticAlertRepositoryImpl implements AgroclimaticAlertRepository {

    private final AgroclimaticAlertPersistenceRepository persistenceRepository;
    private final DomainEventPublisher domainEventPublisher;

    public AgroclimaticAlertRepositoryImpl(
            AgroclimaticAlertPersistenceRepository persistenceRepository,
            DomainEventPublisher domainEventPublisher
    ) {
        this.persistenceRepository = persistenceRepository;
        this.domainEventPublisher = domainEventPublisher;
    }

    @Override
    public AgroclimaticAlert save(AgroclimaticAlert alert) {
        var entity = AgroclimaticAlertPersistenceAssembler.toPersistenceFromDomain(alert);
        var savedEntity = persistenceRepository.save(entity);
        var savedAlert = AgroclimaticAlertPersistenceAssembler.toDomainFromPersistence(savedEntity);

        domainEventPublisher.publishAndClearEvents(alert);

        return savedAlert;
    }

    @Override
    public List<AgroclimaticAlert> findAllByRegion(String region) {
        return persistenceRepository.findAllByRegionOrderByIssuedAtDescIdDesc(region)
                .stream()
                .map(AgroclimaticAlertPersistenceAssembler::toDomainFromPersistence)
                .toList();
    }
}
