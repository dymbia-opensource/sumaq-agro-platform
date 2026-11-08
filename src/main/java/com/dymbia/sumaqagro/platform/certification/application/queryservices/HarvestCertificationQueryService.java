package com.dymbia.sumaqagro.platform.certification.application.queryservices;

import com.dymbia.sumaqagro.platform.certification.application.acl.ExternalFieldManagementService;
import com.dymbia.sumaqagro.platform.certification.domain.model.aggregates.HarvestBatch;
import com.dymbia.sumaqagro.platform.certification.domain.model.aggregates.QualityCertificate;
import com.dymbia.sumaqagro.platform.certification.domain.model.queries.GetHarvestBatchByIdQuery;
import com.dymbia.sumaqagro.platform.certification.domain.model.queries.GetQualityCertificateByNumberQuery;
import com.dymbia.sumaqagro.platform.certification.domain.model.queries.GetQualityCertificateByPublicHashQuery;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Read use cases for harvest batches, quality certificates and public traceability.
 *
 * @since 1.0
 */
public interface HarvestCertificationQueryService {

    /**
     * Finds a harvest batch by its id.
     *
     * @param query batch id
     * @return the batch, or empty when it does not exist
     */
    Optional<HarvestBatch> handle(GetHarvestBatchByIdQuery query);

    /**
     * Finds a quality certificate by its public number.
     *
     * @param query certificate number
     * @return the certificate, or empty when it does not exist
     */
    Optional<QualityCertificate> handle(GetQualityCertificateByNumberQuery query);

    /**
     * Verifies a certificate with its public number and hash and loads the traceability view.
     *
     * @param query certificate number and presented hash
     * @return the public view, or empty when the certificate or its batch does not exist
     */
    Optional<PublicTraceability> handle(GetQualityCertificateByPublicHashQuery query);

    /**
     * Public traceability of one certificate. {@code authentic} is false when the hash does not match or the certificate was revoked.
     *
     * @param certificateNumber public certificate number
     * @param status            certificate lifecycle state
     * @param authentic         whether the presented hash matches an active certificate
     * @param batchCode         source batch code
     * @param cropType          evaluated crop
     * @param qualityCategory   sealed quality category
     * @param qualityScore      assigned quality score
     * @param origin            plot origin, null when field management has no plot
     */
    record PublicTraceability(
            String certificateNumber,
            QualityCertificate.CertificateStatus status,
            boolean authentic,
            String batchCode,
            HarvestBatch.CropType cropType,
            HarvestBatch.QualityCategory qualityCategory,
            BigDecimal qualityScore,
            ExternalFieldManagementService.FieldOrigin origin
    ) {
    }
}
