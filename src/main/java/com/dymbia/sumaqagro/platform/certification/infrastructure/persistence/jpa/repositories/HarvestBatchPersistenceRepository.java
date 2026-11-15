package com.dymbia.sumaqagro.platform.certification.infrastructure.persistence.jpa.repositories;

import com.dymbia.sumaqagro.platform.certification.infrastructure.persistence.jpa.entities.HarvestBatchPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HarvestBatchPersistenceRepository extends JpaRepository<HarvestBatchPersistenceEntity, Long> {

    Optional<HarvestBatchPersistenceEntity> findByBatchCode(String batchCode);

    List<HarvestBatchPersistenceEntity> findByCooperativeIdOrderByIdDesc(Long cooperativeId);

    List<HarvestBatchPersistenceEntity> findByCooperativeIdAndMemberIdInOrderByIdDesc(Long cooperativeId, List<Long> memberIds);

    boolean existsByBatchCode(String batchCode);
}
