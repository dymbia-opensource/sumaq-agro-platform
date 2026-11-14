package com.dymbia.sumaqagro.platform.certification.infrastructure.persistence.jpa.adapters;

import com.dymbia.sumaqagro.platform.certification.domain.model.aggregates.QualityCertificate;
import com.dymbia.sumaqagro.platform.certification.domain.repositories.QualityCertificateRepository;
import com.dymbia.sumaqagro.platform.certification.infrastructure.persistence.jpa.assemblers.QualityCertificatePersistenceAssembler;
import com.dymbia.sumaqagro.platform.certification.infrastructure.persistence.jpa.repositories.QualityCertificatePersistenceRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Adapter that implements the quality certificate repository port with Spring Data JPA.
 * It is the only place where the domain aggregate and the JPA entity meet.
 */
@Repository
public class QualityCertificateRepositoryImpl implements QualityCertificateRepository {

    private final QualityCertificatePersistenceRepository qualityCertificatePersistenceRepository;

    public QualityCertificateRepositoryImpl(QualityCertificatePersistenceRepository qualityCertificatePersistenceRepository) {
        this.qualityCertificatePersistenceRepository = qualityCertificatePersistenceRepository;
    }

    @Override
    public Optional<QualityCertificate> findById(Long id) {
        return qualityCertificatePersistenceRepository.findById(id)
                .map(QualityCertificatePersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public Optional<QualityCertificate> findByCertificateNumber(String certificateNumber) {
        return qualityCertificatePersistenceRepository.findByCertificateNumber(certificateNumber)
                .map(QualityCertificatePersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public Optional<QualityCertificate> findByHarvestBatchId(Long harvestBatchId) {
        return qualityCertificatePersistenceRepository.findByHarvestBatchId(harvestBatchId)
                .map(QualityCertificatePersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public boolean existsByCertificateNumber(String certificateNumber) {
        return qualityCertificatePersistenceRepository.existsByCertificateNumber(certificateNumber);
    }

    @Override
    public boolean existsByHarvestBatchId(Long harvestBatchId) {
        return qualityCertificatePersistenceRepository.existsByHarvestBatchId(harvestBatchId);
    }

    @Override
    public QualityCertificate save(QualityCertificate qualityCertificate) {
        var savedEntity = qualityCertificatePersistenceRepository.saveAndFlush(
                QualityCertificatePersistenceAssembler.toPersistenceFromDomain(qualityCertificate));
        return QualityCertificatePersistenceAssembler.toDomainFromPersistence(savedEntity);
    }
}
