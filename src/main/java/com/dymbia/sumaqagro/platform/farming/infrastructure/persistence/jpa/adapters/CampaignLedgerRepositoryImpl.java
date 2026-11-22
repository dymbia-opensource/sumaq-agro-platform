package com.dymbia.sumaqagro.platform.farming.infrastructure.persistence.jpa.adapters;

import com.dymbia.sumaqagro.platform.farming.domain.model.aggregates.CampaignLedger;
import com.dymbia.sumaqagro.platform.farming.domain.model.valueobjects.CropCampaignId;
import com.dymbia.sumaqagro.platform.farming.domain.repositories.CampaignLedgerRepository;
import com.dymbia.sumaqagro.platform.farming.infrastructure.persistence.jpa.assemblers.CampaignLedgerPersistenceAssembler;
import com.dymbia.sumaqagro.platform.farming.infrastructure.persistence.jpa.repositories.CampaignLedgerPersistenceRepository;
import com.dymbia.sumaqagro.platform.shared.infrastructure.events.DomainEventPublisher;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * JPA adapter of the {@link CampaignLedgerRepository} port.
 * <p>
 * The methods are transactional because the expenses are LAZY: they must be read
 * while the Hibernate session is still open.
 * </p>
 *
 * @since 1.1
 */
@Repository
public class CampaignLedgerRepositoryImpl implements CampaignLedgerRepository {
    private final CampaignLedgerPersistenceRepository campaignLedgerPersistenceRepository;
    private final DomainEventPublisher domainEventPublisher;

    public CampaignLedgerRepositoryImpl(CampaignLedgerPersistenceRepository campaignLedgerPersistenceRepository,
                                        DomainEventPublisher domainEventPublisher) {
        this.campaignLedgerPersistenceRepository = campaignLedgerPersistenceRepository;
        this.domainEventPublisher = domainEventPublisher;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CampaignLedger> findById(Long id) {
        return campaignLedgerPersistenceRepository.findById(id)
                .map(CampaignLedgerPersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CampaignLedger> findByCropCampaignId(CropCampaignId cropCampaignId) {
        return campaignLedgerPersistenceRepository.findByCropCampaignId(cropCampaignId.cropCampaignId())
                .map(CampaignLedgerPersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public boolean existsByCropCampaignId(CropCampaignId cropCampaignId) {
        return campaignLedgerPersistenceRepository.existsByCropCampaignId(cropCampaignId.cropCampaignId());
    }

    @Override
    @Transactional
    public CampaignLedger save(CampaignLedger campaignLedger) {
        var savedEntity = campaignLedgerPersistenceRepository.save(
                CampaignLedgerPersistenceAssembler.toPersistenceFromDomain(campaignLedger));
        var savedLedger = CampaignLedgerPersistenceAssembler.toDomainFromPersistence(savedEntity);
        domainEventPublisher.publishAndClearEvents(campaignLedger);
        return savedLedger;
    }
}
