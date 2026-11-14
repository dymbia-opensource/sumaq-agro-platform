package com.dymbia.sumaqagro.platform.certification.interfaces.rest;

import com.dymbia.sumaqagro.platform.certification.application.commandservices.QualityCertificateCommandService;
import com.dymbia.sumaqagro.platform.certification.application.queryservices.HarvestCertificationQueryService;
import com.dymbia.sumaqagro.platform.certification.domain.model.commands.RevokeQualityCertificateCommand;
import com.dymbia.sumaqagro.platform.certification.domain.model.queries.GetHarvestBatchByIdQuery;
import com.dymbia.sumaqagro.platform.certification.domain.model.queries.GetQualityCertificateByNumberQuery;
import com.dymbia.sumaqagro.platform.certification.infrastructure.documents.PdfQrGeneratorAssembler;
import com.dymbia.sumaqagro.platform.certification.interfaces.rest.resources.IssueQualityCertificateResource;
import com.dymbia.sumaqagro.platform.certification.interfaces.rest.transform.IssueQualityCertificateCommandFromResourceAssembler;
import com.dymbia.sumaqagro.platform.certification.interfaces.rest.transform.QualityCertificateResourceFromEntityAssembler;
import com.dymbia.sumaqagro.platform.shared.application.result.ApplicationError;
import com.dymbia.sumaqagro.platform.shared.interfaces.rest.transform.ProblemDetailFromApplicationErrorAssembler;
import com.dymbia.sumaqagro.platform.shared.interfaces.rest.transform.ResponseEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * REST controller for quality certificates.
 * It only translates HTTP: the rules live in the services and the errors are built by the shared assemblers.
 */
@RestController
@RequestMapping(value = "/api/v1/quality-certificates", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Quality Certificates", description = "Quality certificate issuance, lookup and revocation")
public class QualityCertificatesController {

    private final QualityCertificateCommandService qualityCertificateCommandService;
    private final HarvestCertificationQueryService harvestCertificationQueryService;
    private final String publicBaseUrl;

    public QualityCertificatesController(
            QualityCertificateCommandService qualityCertificateCommandService,
            HarvestCertificationQueryService harvestCertificationQueryService,
            @Value("${sumaqagro.certification.public-base-url}") String publicBaseUrl) {
        this.qualityCertificateCommandService = qualityCertificateCommandService;
        this.harvestCertificationQueryService = harvestCertificationQueryService;
        this.publicBaseUrl = publicBaseUrl;
    }

    /**
     * Issues the quality certificate of a graded harvest batch.
     *
     * @param resource certificate number, batch and issuer
     * @return 201 with the certificate, or the problem detail of the error
     */
    @Operation(summary = "Issue a quality certificate")
    @PostMapping
    public ResponseEntity<?> issueQualityCertificate(@RequestBody IssueQualityCertificateResource resource) {
        var batch = harvestCertificationQueryService.handle(new GetHarvestBatchByIdQuery(resource.harvestBatchId()));
        if (batch.isEmpty()) {
            return ProblemDetailFromApplicationErrorAssembler.toResponseFromApplicationError(
                    ApplicationError.notFound("HarvestBatch", String.valueOf(resource.harvestBatchId())));
        }
        if (batch.get().getQualityCategory() == null) {
            return ProblemDetailFromApplicationErrorAssembler.toResponseFromApplicationError(
                    ApplicationError.businessRuleViolation(
                            "batch-ready-for-certification",
                            "The batch needs an assigned quality score before it can be certified."));
        }
        var issuedAt = Instant.now();
        var document = PdfQrGeneratorAssembler.generate(
                batch.get(),
                resource.certificateNumber(),
                resource.issuedByUserId(),
                issuedAt,
                publicBaseUrl);
        var command = IssueQualityCertificateCommandFromResourceAssembler.toCommandFromResource(
                resource,
                batch.get().getQualityCategory(),
                document.sha256Hash(),
                issuedAt);
        return ResponseEntityAssembler.toResponseEntityFromResult(
                qualityCertificateCommandService.handle(command),
                QualityCertificateResourceFromEntityAssembler::toResourceFromEntity,
                HttpStatus.CREATED);
    }

    /**
     * Gets a quality certificate by its public number.
     *
     * @param certificateNumber the certificate number
     * @return 200 with the certificate, or 404 when it does not exist
     */
    @Operation(summary = "Get a quality certificate by number")
    @GetMapping
    public ResponseEntity<?> getQualityCertificateByNumber(@RequestParam String certificateNumber) {
        return harvestCertificationQueryService.handle(new GetQualityCertificateByNumberQuery(certificateNumber))
                .<ResponseEntity<?>>map(certificate -> ResponseEntity.ok(
                        QualityCertificateResourceFromEntityAssembler.toResourceFromEntity(certificate)))
                .orElseGet(() -> ProblemDetailFromApplicationErrorAssembler.toResponseFromApplicationError(
                        ApplicationError.notFound("QualityCertificate", certificateNumber)));
    }

    /**
     * Revokes an active quality certificate.
     *
     * @param qualityCertificateId the certificate id
     * @return 200 with the revoked certificate, or the problem detail of the error
     */
    @Operation(summary = "Revoke a quality certificate")
    @PostMapping("{qualityCertificateId}/revocation")
    public ResponseEntity<?> revokeQualityCertificate(@PathVariable Long qualityCertificateId) {
        return ResponseEntityAssembler.toResponseEntityFromResult(
                qualityCertificateCommandService.handle(new RevokeQualityCertificateCommand(qualityCertificateId)),
                QualityCertificateResourceFromEntityAssembler::toResourceFromEntity,
                HttpStatus.OK);
    }
}
