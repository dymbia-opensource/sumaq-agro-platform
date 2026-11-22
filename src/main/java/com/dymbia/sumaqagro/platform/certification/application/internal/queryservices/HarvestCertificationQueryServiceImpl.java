package com.dymbia.sumaqagro.platform.certification.application.internal.queryservices;

import com.dymbia.sumaqagro.platform.certification.application.acl.ExternalFarmingService;
import com.dymbia.sumaqagro.platform.certification.application.queryservices.HarvestCertificationQueryService;
import com.dymbia.sumaqagro.platform.certification.domain.model.aggregates.HarvestBatch;
import com.dymbia.sumaqagro.platform.certification.domain.model.aggregates.QualityCertificate;
import com.dymbia.sumaqagro.platform.certification.domain.model.queries.GetAllHarvestBatchesByCooperativeIdAndMemberIdsQuery;
import com.dymbia.sumaqagro.platform.certification.domain.model.queries.GetAllHarvestBatchesByCooperativeIdQuery;
import com.dymbia.sumaqagro.platform.certification.domain.model.queries.GetHarvestBatchByIdQuery;
import com.dymbia.sumaqagro.platform.certification.domain.model.queries.GetQualityCertificateByHarvestBatchIdQuery;
import com.dymbia.sumaqagro.platform.certification.domain.model.queries.GetQualityCertificateByIdQuery;
import com.dymbia.sumaqagro.platform.certification.domain.model.queries.GetQualityCertificateByNumberQuery;
import com.dymbia.sumaqagro.platform.certification.domain.model.queries.GetQualityCertificateByPublicHashQuery;
import com.dymbia.sumaqagro.platform.certification.domain.repositories.HarvestBatchRepository;
import com.dymbia.sumaqagro.platform.certification.domain.repositories.QualityCertificateRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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
    private final ExternalFarmingService externalFarmingService;

    public HarvestCertificationQueryServiceImpl(HarvestBatchRepository harvestBatchRepository,
                                                QualityCertificateRepository qualityCertificateRepository,
                                                ExternalFarmingService externalFarmingService) {
        this.harvestBatchRepository = harvestBatchRepository;
        this.qualityCertificateRepository = qualityCertificateRepository;
        this.externalFarmingService = externalFarmingService;
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

    @Override
    public List<HarvestBatch> handle(GetAllHarvestBatchesByCooperativeIdQuery query) {
        return harvestBatchRepository.findByCooperativeId(query.cooperativeId());
    }

    @Override
    public List<HarvestBatch> handle(GetAllHarvestBatchesByCooperativeIdAndMemberIdsQuery query) {
        if (query.memberIds().isEmpty()) return List.of();
        return harvestBatchRepository.findByCooperativeIdAndMemberIdIn(query.cooperativeId(), query.memberIds());
    }

    @Override
    public Optional<QualityCertificate> handle(GetQualityCertificateByIdQuery query) {
        return qualityCertificateRepository.findById(query.qualityCertificateId());
    }

    @Override
    public Optional<QualityCertificate> handle(GetQualityCertificateByHarvestBatchIdQuery query) {
        return qualityCertificateRepository.findByHarvestBatchId(query.harvestBatchId());
    }

    private PublicTraceability toPublicTraceability(QualityCertificate certificate, HarvestBatch batch, String presentedHash) {
        if (!certificate.verify(presentedHash)) {
            return new PublicTraceability(certificate.getCertificateNumber(), certificate.getStatus(),
                    false, null, null, null, null, null);
        }
        ExternalFarmingService.FieldOrigin origin = externalFarmingService
                .findOriginByPlotId(batch.getPlotId())
                .orElse(null);
        return new PublicTraceability(
                certificate.getCertificateNumber(),
                certificate.getStatus(),
                true,
                batch.getBatchCode(),
                batch.getCropType(),
                certificate.getQualityCategory(),
                batch.getQualityScore(),
                origin);
    }
}
