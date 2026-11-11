package com.dymbia.sumaqagro.platform.crop.infrastructure.persistence.jpa.adapters;

import com.dymbia.sumaqagro.platform.crop.domain.model.aggregates.ClimateForecast;
import com.dymbia.sumaqagro.platform.crop.domain.repositories.ClimateForecastRepository;
import com.dymbia.sumaqagro.platform.crop.infrastructure.persistence.jpa.assemblers.ClimateForecastPersistenceAssembler;
import com.dymbia.sumaqagro.platform.crop.infrastructure.persistence.jpa.repositories.ClimateForecastPersistenceRepository;
import com.dymbia.sumaqagro.platform.shared.infrastructure.events.DomainEventPublisher;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Implements the climate forecast domain repository using Spring Data JPA.
 */
@Repository
public class ClimateForecastRepositoryImpl implements ClimateForecastRepository {

    private final ClimateForecastPersistenceRepository persistenceRepository;
    private final DomainEventPublisher domainEventPublisher;

    public ClimateForecastRepositoryImpl(ClimateForecastPersistenceRepository persistenceRepository, DomainEventPublisher domainEventPublisher) {
        this.persistenceRepository = persistenceRepository;
        this.domainEventPublisher = domainEventPublisher;
    }

    @Override
    public ClimateForecast save(ClimateForecast forecast) {
        var entity = ClimateForecastPersistenceAssembler.toPersistenceFromDomain(forecast);
        var savedEntity = persistenceRepository.save(entity);
        var savedForecast = ClimateForecastPersistenceAssembler.toDomainFromPersistence(savedEntity);

        domainEventPublisher.publishAndClearEvents(forecast);

        return savedForecast;
    }

    @Override
    public List<ClimateForecast> findAllByRegion(String region) {
        return persistenceRepository.findAllByRegionOrderByForecastDateDescIdDesc(region)
                .stream()
                .map(ClimateForecastPersistenceAssembler::toDomainFromPersistence)
                .toList();
    }
}
