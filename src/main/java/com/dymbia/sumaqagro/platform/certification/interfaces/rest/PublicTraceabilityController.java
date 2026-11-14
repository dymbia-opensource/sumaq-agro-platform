package com.dymbia.sumaqagro.platform.certification.interfaces.rest;

import com.dymbia.sumaqagro.platform.certification.application.queryservices.HarvestCertificationQueryService;
import com.dymbia.sumaqagro.platform.certification.domain.model.queries.GetQualityCertificateByPublicHashQuery;
import com.dymbia.sumaqagro.platform.certification.infrastructure.documents.PdfQrGeneratorAssembler;
import com.dymbia.sumaqagro.platform.certification.interfaces.rest.transform.PublicTraceabilityResourceFromEntityAssembler;
import com.dymbia.sumaqagro.platform.shared.application.result.ApplicationError;
import com.dymbia.sumaqagro.platform.shared.interfaces.rest.transform.ProblemDetailFromApplicationErrorAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Public verification of a quality certificate.
 * The QR code opens this endpoint with the certificate number and its hash.
 */
@RestController
@RequestMapping(value = PdfQrGeneratorAssembler.PUBLIC_VERIFICATION_PATH, produces = APPLICATION_JSON_VALUE)
@Tag(name = "Public Traceability", description = "Public verification of a quality certificate")
public class PublicTraceabilityController {

    private final HarvestCertificationQueryService harvestCertificationQueryService;

    public PublicTraceabilityController(HarvestCertificationQueryService harvestCertificationQueryService) {
        this.harvestCertificationQueryService = harvestCertificationQueryService;
    }

    /**
     * Verifies a certificate with the number and hash encoded in its QR code.
     *
     * @param number public certificate number
     * @param hash   SHA-256 hash presented by the visitor
     * @return 200 with the traceability view, or 404 when the certificate does not exist
     */
    @Operation(summary = "Verify a quality certificate")
    @GetMapping
    public ResponseEntity<?> verify(@RequestParam String number, @RequestParam String hash) {
        return harvestCertificationQueryService.handle(new GetQualityCertificateByPublicHashQuery(number, hash))
                .<ResponseEntity<?>>map(traceability -> ResponseEntity.ok(
                        PublicTraceabilityResourceFromEntityAssembler.toResourceFromEntity(traceability)))
                .orElseGet(() -> ProblemDetailFromApplicationErrorAssembler.toResponseFromApplicationError(
                        ApplicationError.notFound("QualityCertificate", number)));
    }
}
