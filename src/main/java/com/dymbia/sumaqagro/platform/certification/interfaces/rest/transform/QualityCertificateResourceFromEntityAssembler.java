package com.dymbia.sumaqagro.platform.certification.interfaces.rest.transform;

import com.dymbia.sumaqagro.platform.certification.domain.model.aggregates.QualityCertificate;
import com.dymbia.sumaqagro.platform.certification.interfaces.rest.resources.QualityCertificateResource;

import java.util.Objects;

/**
 * Converts the QualityCertificate aggregate into its resource.
 */
public final class QualityCertificateResourceFromEntityAssembler {

    private QualityCertificateResourceFromEntityAssembler() {
    }

    public static QualityCertificateResource toResourceFromEntity(QualityCertificate entity) {
        Objects.requireNonNull(entity, "Quality certificate is required.");
        return new QualityCertificateResource(
                entity.getId(),
                entity.getCertificateNumber(),
                entity.getHarvestBatchId(),
                entity.getQualityCategory().name(),
                entity.getCertificateHash(),
                entity.getIssuedByUserId(),
                entity.getIssuedAt(),
                entity.getStatus().name());
    }
}
