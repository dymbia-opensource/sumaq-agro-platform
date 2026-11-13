package com.dymbia.sumaqagro.platform.crop.infrastructure.persistence.jpa.adapters;

import com.dymbia.sumaqagro.platform.crop.domain.model.aggregates.SatelliteObservation;
import com.dymbia.sumaqagro.platform.crop.domain.repositories.SatelliteObservationRepository;
import com.dymbia.sumaqagro.platform.crop.infrastructure.persistence.jpa.assemblers.SatelliteObservationPersistenceAssembler;
import com.dymbia.sumaqagro.platform.crop.infrastructure.persistence.jpa.entities.SatelliteObservationPersistenceEntity;
import com.dymbia.sumaqagro.platform.crop.infrastructure.persistence.jpa.repositories.SatelliteObservationPersistenceRepository;
import com.dymbia.sumaqagro.platform.shared.infrastructure.events.DomainEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
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

    /**
     * Saves an imported satellite observation.
     *
     * @param observation the satellite observation to save
     * @return the saved satellite observation
     */
    @Override
    public SatelliteObservation saveImported(SatelliteObservation observation) {
        var existing = persistenceRepository.findFirstByPlotIdAndCapturedAtAndSourceOrderByIdAsc(
                observation.getPlotId(), observation.getCapturedAt(), observation.getSource());
        if (existing.isPresent()) {
            return SatelliteObservationPersistenceAssembler.toDomainFromPersistence(existing.get());
        }

        var identity = "%s/%s/%s".formatted(
                observation.getPlotId(), observation.getCapturedAt(), observation.getSource());
        var importKey = DigestUtils.md5DigestAsHex(identity.getBytes(StandardCharsets.UTF_8));
        var entity = SatelliteObservationPersistenceAssembler.toPersistenceFromDomain(observation);
        entity.setImportKey(importKey);

        SatelliteObservationPersistenceEntity savedEntity;
        try {
            savedEntity = persistenceRepository.saveAndFlush(entity);
        } catch (DataIntegrityViolationException e) {
            // Another request may have committed the same capture after our lookup.
            return persistenceRepository.findByImportKey(importKey)
                    .map(SatelliteObservationPersistenceAssembler::toDomainFromPersistence)
                    .orElseThrow(() -> e);
        }
        domainEventPublisher.publishAndClearEvents(observation);
        return SatelliteObservationPersistenceAssembler.toDomainFromPersistence(savedEntity);
    }

    @Override
    public List<SatelliteObservation> findAllByPlotId(Long plotId) {
        return persistenceRepository.findAllByPlotIdOrderByCapturedAtDescIdDesc(plotId)
                .stream()
                .map(SatelliteObservationPersistenceAssembler::toDomainFromPersistence)
                .toList();
    }
}
