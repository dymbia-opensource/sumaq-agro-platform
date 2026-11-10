package com.dymbia.sumaqagro.platform.crop.infrastructure.persistence.jpa.adapters;

import com.dymbia.sumaqagro.platform.crop.domain.model.aggregates.PestReport;
import com.dymbia.sumaqagro.platform.crop.domain.repositories.PestReportRepository;
import com.dymbia.sumaqagro.platform.crop.infrastructure.persistence.jpa.assemblers.PestReportPersistenceAssembler;
import com.dymbia.sumaqagro.platform.crop.infrastructure.persistence.jpa.repositories.PestReportPersistenceRepository;
import com.dymbia.sumaqagro.platform.shared.infrastructure.events.DomainEventPublisher;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Implements the pest report domain repository using Spring Data JPA.
 */
@Repository
public class PestReportRepositoryImpl implements PestReportRepository {

    private final PestReportPersistenceRepository persistenceRepository;
    private final DomainEventPublisher domainEventPublisher;

    public PestReportRepositoryImpl(PestReportPersistenceRepository persistenceRepository, DomainEventPublisher domainEventPublisher) {
        this.persistenceRepository = persistenceRepository;
        this.domainEventPublisher = domainEventPublisher;
    }

    @Override
    public PestReport save(PestReport pestReport) {

        var entity = PestReportPersistenceAssembler.toPersistenceFromDomain(pestReport);

        var savedEntity = persistenceRepository.save(entity);

        var savedReport = PestReportPersistenceAssembler.toDomainFromPersistence(savedEntity);

        domainEventPublisher.publishAndClearEvents(pestReport);

        return savedReport;
    }

    @Override
    public Optional<PestReport> findById(Long pestReportId) {
        return persistenceRepository.findById(pestReportId)
                .map(PestReportPersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public List<PestReport> findAllByPlotId(Long plotId) {
        return persistenceRepository.findAllByPlotId(plotId)
                .stream()
                .map(PestReportPersistenceAssembler::toDomainFromPersistence)
                .toList();
    }

    @Override
    public void deleteById(Long pestReportId) {
        persistenceRepository.deleteById(pestReportId);
    }
}