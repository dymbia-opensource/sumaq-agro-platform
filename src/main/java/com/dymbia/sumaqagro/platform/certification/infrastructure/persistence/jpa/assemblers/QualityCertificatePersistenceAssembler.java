package com.dymbia.sumaqagro.platform.certification.infrastructure.persistence.jpa.assemblers;

import com.dymbia.sumaqagro.platform.certification.domain.model.aggregates.QualityCertificate;
import com.dymbia.sumaqagro.platform.certification.infrastructure.persistence.jpa.entities.QualityCertificatePersistenceEntity;

/**
 * Converts a quality certificate between the domain aggregate and its JPA entity.
 */
public final class QualityCertificatePersistenceAssembler {

    private QualityCertificatePersistenceAssembler() {
    }

    /**
     * Builds the domain aggregate from a stored entity.
     *
     * @param entity the entity read from the database
     * @return the quality certificate with the stored state
     */
    public static QualityCertificate toDomainFromPersistence(QualityCertificatePersistenceEntity entity) {
        return QualityCertificate.reconstitute(
                entity.getId(),
                entity.getCertificateNumber(),
                entity.getHarvestBatchId(),
                entity.getQualityCategory(),
                entity.getCertificateHash(),
                entity.getIssuedByUserId(),
                entity.getIssuedAt(),
                entity.getStatus());
    }

    /**
     * Builds the JPA entity from a domain aggregate.
     * If the certificate has no id the entity is inserted, otherwise it is updated.
     *
     * @param qualityCertificate the domain aggregate
     * @return the entity ready to save
     */
    public static QualityCertificatePersistenceEntity toPersistenceFromDomain(QualityCertificate qualityCertificate) {
        var entity = new QualityCertificatePersistenceEntity();
        entity.setId(qualityCertificate.getId());
        entity.setCertificateNumber(qualityCertificate.getCertificateNumber());
        entity.setHarvestBatchId(qualityCertificate.getHarvestBatchId());
        entity.setQualityCategory(qualityCertificate.getQualityCategory());
        entity.setCertificateHash(qualityCertificate.getCertificateHash());
        entity.setIssuedByUserId(qualityCertificate.getIssuedByUserId());
        entity.setIssuedAt(qualityCertificate.getIssuedAt());
        entity.setStatus(qualityCertificate.getStatus());
        return entity;
    }
}
