package com.dymbia.sumaqagro.platform.farming.infrastructure.persistence.jpa.repositories;

import com.dymbia.sumaqagro.platform.farming.domain.model.valueobjects.CampaignStatus;
import com.dymbia.sumaqagro.platform.farming.infrastructure.persistence.jpa.entities.CropCampaignPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data repository of the crop campaign JPA entity.
 */
@Repository
public interface CropCampaignPersistenceRepository extends JpaRepository<CropCampaignPersistenceEntity, Long> {

    List<CropCampaignPersistenceEntity> findAllByPlotId(Long plotId);

    boolean existsByPlotIdAndStatus(Long plotId, CampaignStatus status);
}
