package com.dymbia.sumaqagro.platform.certification.infrastructure.persistence.jpa.repositories;

import com.dymbia.sumaqagro.platform.certification.infrastructure.persistence.jpa.entities.QualityCertificatePersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QualityCertificatePersistenceRepository extends JpaRepository<QualityCertificatePersistenceEntity, Long> {

    Optional<QualityCertificatePersistenceEntity> findByCertificateNumber(String certificateNumber);

    Optional<QualityCertificatePersistenceEntity> findByHarvestBatchId(Long harvestBatchId);

    boolean existsByCertificateNumber(String certificateNumber);

    boolean existsByHarvestBatchId(Long harvestBatchId);
}
