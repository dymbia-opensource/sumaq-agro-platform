package com.dymbia.sumaqagro.platform.certification.application.internal.queryservices;

import com.dymbia.sumaqagro.platform.certification.application.acl.ExternalFieldManagementService;
import com.dymbia.sumaqagro.platform.certification.application.queryservices.HarvestCertificationQueryService;
import com.dymbia.sumaqagro.platform.certification.domain.model.aggregates.HarvestBatch;
import com.dymbia.sumaqagro.platform.certification.domain.model.aggregates.QualityCertificate;
import com.dymbia.sumaqagro.platform.certification.domain.model.queries.GetHarvestBatchByIdQuery;
import com.dymbia.sumaqagro.platform.certification.domain.model.queries.GetQualityCertificateByNumberQuery;
import com.dymbia.sumaqagro.platform.certification.domain.model.queries.GetQualityCertificateByPublicHashQuery;
import com.dymbia.sumaqagro.platform.certification.domain.repositories.HarvestBatchRepository;
import com.dymbia.sumaqagro.platform.certification.domain.repositories.QualityCertificateRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Reads harvest batches and quality certificates.
 * Public verification also asks field management for the plot origin.
 *
 * @since 1.0
 */
@Service
@Transactional(readOnly = true)
public class HarvestCertificationQueryServiceImpl implements HarvestCertificationQueryService {

    private final HarvestBatchRepository harvestBatchRepository;
    private final QualityCertificateRepository qualityCertificateRepository;
    private final ExternalFieldManagementService externalFieldManagementService;

    public HarvestCertificationQueryServiceImpl(HarvestBatchRepository harvestBatchRepository,
                                                QualityCertificateRepository qualityCertificateRepository,
                                                ExternalFieldManagementService externalFieldManagementService) {
        this.harvestBatchRepository = harvestBatchRepository;
        this.qualityCertificateRepository = qualityCertificateRepository;
        this.externalFieldManagementService = externalFieldManagementService;
    }

    @Override
    public Optional<HarvestBatch> handle(GetHarvestBatchByIdQuery query) {
        return harvestBatchRepository.findById(query.harvestBatchId());
    }

    @Override
    public Optional<QualityCertificate> handle(GetQualityCertificateByNumberQuery query) {
        return qualityCertificateRepository.findByCertificateNumber(query.certificateNumber());
    }

    @Override
    public Optional<PublicTraceability> handle(GetQualityCertificateByPublicHashQuery query) {
        return qualityCertificateRepository.findByCertificateNumber(query.certificateNumber())
                .flatMap(certificate -> harvestBatchRepository.findById(certificate.getHarvestBatchId())
                        .map(batch -> toPublicTraceability(certificate, batch, query.certificateHash())));
    }

    private PublicTraceability toPublicTraceability(QualityCertificate certificate, HarvestBatch batch, String presentedHash) {
        ExternalFieldManagementService.FieldOrigin origin = externalFieldManagementService
                .findOriginByPlotId(batch.getPlotId())
                .orElse(null);
        return new PublicTraceability(
                certificate.getCertificateNumber(),
                certificate.getStatus(),
                certificate.verify(presentedHash),
                batch.getBatchCode(),
                batch.getCropType(),
                certificate.getQualityCategory(),
                batch.getQualityScore(),
                origin);
    }
}
