package com.dymbia.sumaqagro.platform.certification.infrastructure.persistence.jpa.repositories;

import com.dymbia.sumaqagro.platform.certification.infrastructure.persistence.jpa.entities.HarvestBatchPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HarvestBatchPersistenceRepository extends JpaRepository<HarvestBatchPersistenceEntity, Long> {

    Optional<HarvestBatchPersistenceEntity> findByBatchCode(String batchCode);

    boolean existsByBatchCode(String batchCode);
}
