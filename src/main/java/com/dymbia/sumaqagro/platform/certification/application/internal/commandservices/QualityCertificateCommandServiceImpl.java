package com.dymbia.sumaqagro.platform.certification.application.internal.commandservices;

import com.dymbia.sumaqagro.platform.certification.application.commandservices.QualityCertificateCommandService;
import com.dymbia.sumaqagro.platform.certification.domain.model.aggregates.QualityCertificate;
import com.dymbia.sumaqagro.platform.certification.domain.model.commands.IssueQualityCertificateCommand;
import com.dymbia.sumaqagro.platform.certification.domain.model.commands.RevokeQualityCertificateCommand;
import com.dymbia.sumaqagro.platform.certification.domain.repositories.HarvestBatchRepository;
import com.dymbia.sumaqagro.platform.certification.domain.repositories.QualityCertificateRepository;
import com.dymbia.sumaqagro.platform.certification.infrastructure.documents.PdfQrGeneratorAssembler;
import com.dymbia.sumaqagro.platform.shared.application.result.ApplicationError;
import com.dymbia.sumaqagro.platform.shared.application.result.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Issues and revokes quality certificates.
 * Issuing seals the batch with the hash produced by the PDF and QR generator.
 *
 * @since 1.0
 */
@Service
public class QualityCertificateCommandServiceImpl implements QualityCertificateCommandService {

    private static final String DUPLICATE_CERTIFICATE = "A certificate with this number or batch already exists.";

    private final QualityCertificateRepository qualityCertificateRepository;
    private final HarvestBatchRepository harvestBatchRepository;
    private final String publicBaseUrl;

    public QualityCertificateCommandServiceImpl(
            QualityCertificateRepository qualityCertificateRepository,
            HarvestBatchRepository harvestBatchRepository,
            @Value("${sumaqagro.certification.public-base-url}") String publicBaseUrl) {
        this.qualityCertificateRepository = qualityCertificateRepository;
        this.harvestBatchRepository = harvestBatchRepository;
        this.publicBaseUrl = publicBaseUrl;
    }

    @Override
    @Transactional
    public Result<QualityCertificate, ApplicationError> handle(IssueQualityCertificateCommand command) {
        var batch = harvestBatchRepository.findById(command.harvestBatchId());
        if (batch.isEmpty()) {
            return Result.failure(ApplicationError.notFound("HarvestBatch", String.valueOf(command.harvestBatchId())));
        }
        if (!batch.get().isReadyForCertification()) {
            return Result.failure(ApplicationError.businessRuleViolation(
                    "batch-ready-for-certification", "The batch needs an assigned quality score before it can be certified."));
        }
        if (batch.get().getQualityCategory() != command.qualityCategory()) {
            return Result.failure(ApplicationError.businessRuleViolation(
                    "quality-category", "The certificate category does not match the batch."));
        }
        if (qualityCertificateRepository.existsByCertificateNumber(command.certificateNumber())
                || qualityCertificateRepository.existsByHarvestBatchId(command.harvestBatchId())) {
            return Result.failure(ApplicationError.conflict("QualityCertificate", DUPLICATE_CERTIFICATE));
        }
        try {
            var document = PdfQrGeneratorAssembler.generate(
                    batch.get(),
                    command.certificateNumber(),
                    command.issuedByUserId(),
                    command.issuedAt(),
                    publicBaseUrl);
            if (!document.sha256Hash().equalsIgnoreCase(command.certificateHash())) {
                return Result.failure(ApplicationError.validationError(
                        "certificateHash", "The certificate hash does not match the sealed content."));
            }
            batch.get().markCertified();
            harvestBatchRepository.save(batch.get());
            QualityCertificate certificate = qualityCertificateRepository.save(new QualityCertificate(
                    command.certificateNumber(),
                    command.harvestBatchId(),
                    command.qualityCategory(),
                    document.sha256Hash(),
                    command.issuedByUserId(),
                    command.issuedAt()));
            return Result.success(certificate);
        } catch (IllegalStateException | IllegalArgumentException exception) {
            return Result.failure(ApplicationError.businessRuleViolation("quality-certificate", exception.getMessage()));
        } catch (DataIntegrityViolationException exception) {
            return Result.failure(ApplicationError.conflict("QualityCertificate", DUPLICATE_CERTIFICATE));
        }
    }

    @Override
    @Transactional
    public Result<QualityCertificate, ApplicationError> handle(RevokeQualityCertificateCommand command) {
        return qualityCertificateRepository.findById(command.qualityCertificateId())
                .map(this::revokeAndSave)
                .orElseGet(() -> Result.failure(ApplicationError.notFound(
                        "QualityCertificate", String.valueOf(command.qualityCertificateId()))));
    }

    private Result<QualityCertificate, ApplicationError> revokeAndSave(QualityCertificate certificate) {
        try {
            certificate.revoke();
            return Result.success(qualityCertificateRepository.save(certificate));
        } catch (IllegalStateException exception) {
            return Result.failure(ApplicationError.businessRuleViolation("quality-certificate", exception.getMessage()));
        }
    }
}
