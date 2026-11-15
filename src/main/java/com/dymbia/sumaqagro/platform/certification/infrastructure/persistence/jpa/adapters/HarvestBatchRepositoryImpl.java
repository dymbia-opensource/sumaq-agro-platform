package com.dymbia.sumaqagro.platform.certification.infrastructure.persistence.jpa.adapters;

import com.dymbia.sumaqagro.platform.certification.domain.model.aggregates.HarvestBatch;
import com.dymbia.sumaqagro.platform.certification.domain.repositories.HarvestBatchRepository;
import com.dymbia.sumaqagro.platform.certification.infrastructure.persistence.jpa.assemblers.HarvestBatchPersistenceAssembler;
import com.dymbia.sumaqagro.platform.certification.infrastructure.persistence.jpa.repositories.HarvestBatchPersistenceRepository;
import com.dymbia.sumaqagro.platform.shared.infrastructure.events.DomainEventPublisher;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Adapter that implements the harvest batch repository port with Spring Data JPA.
 * It is the only place where the domain aggregate and the JPA entity meet.
 * Domain events registered on the batch are published after a successful save.
 */
@Repository
public class HarvestBatchRepositoryImpl implements HarvestBatchRepository {

    private final HarvestBatchPersistenceRepository harvestBatchPersistenceRepository;
    private final DomainEventPublisher domainEventPublisher;

    public HarvestBatchRepositoryImpl(HarvestBatchPersistenceRepository harvestBatchPersistenceRepository,
                                      DomainEventPublisher domainEventPublisher) {
        this.harvestBatchPersistenceRepository = harvestBatchPersistenceRepository;
        this.domainEventPublisher = domainEventPublisher;
    }

    @Override
    public Optional<HarvestBatch> findById(Long id) {
        return harvestBatchPersistenceRepository.findById(id)
                .map(HarvestBatchPersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public Optional<HarvestBatch> findByBatchCode(String batchCode) {
        return harvestBatchPersistenceRepository.findByBatchCode(batchCode)
                .map(HarvestBatchPersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public boolean existsByBatchCode(String batchCode) {
        return harvestBatchPersistenceRepository.existsByBatchCode(batchCode);
    }

    @Override
    public HarvestBatch save(HarvestBatch harvestBatch) {
        var savedEntity = harvestBatchPersistenceRepository.saveAndFlush(
                HarvestBatchPersistenceAssembler.toPersistenceFromDomain(harvestBatch));
        domainEventPublisher.publishAndClearEvents(harvestBatch);
        return HarvestBatchPersistenceAssembler.toDomainFromPersistence(savedEntity);
    }
}
