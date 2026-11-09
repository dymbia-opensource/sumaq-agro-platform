package com.dymbia.sumaqagro.platform.crop.infrastructure.persistence.jpa.adapters;

import com.dymbia.sumaqagro.platform.crop.domain.model.aggregates.FieldInspection;
import com.dymbia.sumaqagro.platform.crop.domain.repositories.FieldInspectionRepository;
import com.dymbia.sumaqagro.platform.crop.infrastructure.persistence.jpa.assemblers.FieldInspectionPersistenceAssembler;
import com.dymbia.sumaqagro.platform.crop.infrastructure.persistence.jpa.repositories.FieldInspectionPersistenceRepository;
import com.dymbia.sumaqagro.platform.shared.infrastructure.events.DomainEventPublisher;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Implements the field inspection domain repository using Spring Data JPA.
 */
@Repository
public class FieldInspectionRepositoryImpl implements FieldInspectionRepository {

    private final FieldInspectionPersistenceRepository persistenceRepository;
    private final DomainEventPublisher domainEventPublisher;

    public FieldInspectionRepositoryImpl(FieldInspectionPersistenceRepository persistenceRepository, DomainEventPublisher domainEventPublisher) {
        this.persistenceRepository = persistenceRepository;
        this.domainEventPublisher = domainEventPublisher;
    }

    @Override
    public FieldInspection save(FieldInspection inspection) {
        var entity = FieldInspectionPersistenceAssembler
                .toPersistenceFromDomain(inspection);

        var savedEntity = persistenceRepository.save(entity);

        var savedInspection = FieldInspectionPersistenceAssembler
                .toDomainFromPersistence(savedEntity);

        domainEventPublisher.publishAndClearEvents(inspection);

        return savedInspection;
    }

    @Override
    public Optional<FieldInspection> findById(Long fieldInspectionId) {
        return persistenceRepository.findById(fieldInspectionId)
                .map(FieldInspectionPersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public List<FieldInspection> findAllByPlotId(Long plotId) {
        return persistenceRepository.findAllByPlotId(plotId)
                .stream()
                .map(FieldInspectionPersistenceAssembler::toDomainFromPersistence)
                .toList();
    }

    @Override
    public boolean existsByPestReportId(Long pestReportId) {
        return persistenceRepository.existsByPestReportId(pestReportId);
    }
}