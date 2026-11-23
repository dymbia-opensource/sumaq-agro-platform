package com.dymbia.sumaqagro.platform.farming.infrastructure.persistence.jpa.repositories;

import com.dymbia.sumaqagro.platform.farming.infrastructure.persistence.jpa.entities.CampaignLedgerPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data repository of the cost ledger JPA mapping.
 *
 * @since 1.1
 */
@Repository
public interface CampaignLedgerPersistenceRepository extends JpaRepository<CampaignLedgerPersistenceEntity, Long> {

    Optional<CampaignLedgerPersistenceEntity> findByCropCampaignId(Long cropCampaignId);

    boolean existsByCropCampaignId(Long cropCampaignId);
}
