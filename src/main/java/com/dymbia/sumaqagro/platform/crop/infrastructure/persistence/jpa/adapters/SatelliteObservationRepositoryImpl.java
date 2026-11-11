package com.dymbia.sumaqagro.platform.crop.infrastructure.persistence.jpa.adapters;

import com.dymbia.sumaqagro.platform.crop.domain.model.aggregates.SatelliteObservation;
import com.dymbia.sumaqagro.platform.crop.domain.repositories.SatelliteObservationRepository;
import com.dymbia.sumaqagro.platform.crop.infrastructure.persistence.jpa.assemblers.SatelliteObservationPersistenceAssembler;
import com.dymbia.sumaqagro.platform.crop.infrastructure.persistence.jpa.repositories.SatelliteObservationPersistenceRepository;
import com.dymbia.sumaqagro.platform.shared.infrastructure.events.DomainEventPublisher;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Implements the satellite observation domain repository using Spring Data JPA.
 */
@Repository
public class SatelliteObservationRepositoryImpl implements SatelliteObservationRepository {

    private final SatelliteObservationPersistenceRepository persistenceRepository;
    private final DomainEventPublisher domainEventPublisher;

    public SatelliteObservationRepositoryImpl(SatelliteObservationPersistenceRepository persistenceRepository, DomainEventPublisher domainEventPublisher) {
        this.persistenceRepository = persistenceRepository;
        this.domainEventPublisher = domainEventPublisher;
    }

    @Override
    public SatelliteObservation save(SatelliteObservation observation) {
        var entity = SatelliteObservationPersistenceAssembler
                .toPersistenceFromDomain(observation);

        var savedEntity = persistenceRepository.save(entity);
        var savedObservation = SatelliteObservationPersistenceAssembler
                .toDomainFromPersistence(savedEntity);

        domainEventPublisher.publishAndClearEvents(observation);

        return savedObservation;
    }

    @Override
    public List<SatelliteObservation> findAllByPlotId(Long plotId) {
        return persistenceRepository.findAllByPlotIdOrderByCapturedAtDescIdDesc(plotId)
                .stream()
                .map(SatelliteObservationPersistenceAssembler::toDomainFromPersistence)
                .toList();
    }
}
