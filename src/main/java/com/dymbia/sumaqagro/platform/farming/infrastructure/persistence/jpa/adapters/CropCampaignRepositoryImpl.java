package com.dymbia.sumaqagro.platform.farming.infrastructure.persistence.jpa.adapters;

import com.dymbia.sumaqagro.platform.farming.domain.model.aggregates.CropCampaign;
import com.dymbia.sumaqagro.platform.farming.domain.model.valueobjects.CampaignStatus;
import com.dymbia.sumaqagro.platform.farming.domain.model.valueobjects.FieldPlotId;
import com.dymbia.sumaqagro.platform.farming.domain.repositories.CropCampaignRepository;
import com.dymbia.sumaqagro.platform.farming.infrastructure.persistence.jpa.assemblers.CropCampaignPersistenceAssembler;
import com.dymbia.sumaqagro.platform.farming.infrastructure.persistence.jpa.repositories.CropCampaignPersistenceRepository;
import com.dymbia.sumaqagro.platform.shared.infrastructure.events.DomainEventPublisher;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Adapter that implements the crop campaign repository port with Spring Data JPA.
 * After saving, it publishes the domain events registered on the aggregate.
 */
@Repository
public class CropCampaignRepositoryImpl implements CropCampaignRepository {
    private final CropCampaignPersistenceRepository cropCampaignPersistenceRepository;
    private final DomainEventPublisher domainEventPublisher;

    public CropCampaignRepositoryImpl(CropCampaignPersistenceRepository cropCampaignPersistenceRepository,
                                      DomainEventPublisher domainEventPublisher) {
        this.cropCampaignPersistenceRepository = cropCampaignPersistenceRepository;
        this.domainEventPublisher = domainEventPublisher;
    }

    @Override
    public Optional<CropCampaign> findById(Long id) {
        return cropCampaignPersistenceRepository.findById(id)
                .map(CropCampaignPersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public List<CropCampaign> findAllByFieldPlotId(FieldPlotId fieldPlotId) {
        return cropCampaignPersistenceRepository.findAllByPlotId(fieldPlotId.fieldPlotId()).stream()
                .map(CropCampaignPersistenceAssembler::toDomainFromPersistence).toList();
    }

    @Override
    public boolean existsByFieldPlotIdAndStatus(FieldPlotId fieldPlotId, CampaignStatus status) {
        return cropCampaignPersistenceRepository.existsByPlotIdAndStatus(fieldPlotId.fieldPlotId(), status);
    }

    @Override
    public CropCampaign save(CropCampaign cropCampaign) {
        var savedEntity = cropCampaignPersistenceRepository.save(
                CropCampaignPersistenceAssembler.toPersistenceFromDomain(cropCampaign));
        var savedCropCampaign = CropCampaignPersistenceAssembler.toDomainFromPersistence(savedEntity);
        domainEventPublisher.publishAndClearEvents(cropCampaign);
        return savedCropCampaign;
    }
}
