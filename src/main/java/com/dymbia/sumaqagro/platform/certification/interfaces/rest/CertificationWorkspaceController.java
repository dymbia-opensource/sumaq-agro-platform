package com.dymbia.sumaqagro.platform.certification.interfaces.rest;

import com.dymbia.sumaqagro.platform.certification.domain.model.aggregates.HarvestBatch;
import com.dymbia.sumaqagro.platform.certification.domain.model.valueobjects.CertificateStatus;
import com.dymbia.sumaqagro.platform.certification.domain.repositories.HarvestBatchRepository;
import com.dymbia.sumaqagro.platform.certification.domain.repositories.QualityCertificateRepository;
import com.dymbia.sumaqagro.platform.certification.infrastructure.documents.PdfQrGeneratorAssembler;
import com.dymbia.sumaqagro.platform.certification.interfaces.rest.resources.HarvestBatchResource;
import com.dymbia.sumaqagro.platform.certification.interfaces.rest.resources.QualityCertificateResource;
import com.dymbia.sumaqagro.platform.certification.interfaces.rest.transform.HarvestBatchResourceFromEntityAssembler;
import com.dymbia.sumaqagro.platform.certification.interfaces.rest.transform.QualityCertificateResourceFromEntityAssembler;
import com.dymbia.sumaqagro.platform.iam.infrastructure.authorization.sfs.model.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class CertificationWorkspaceController {
    private final HarvestBatchRepository batches;
    private final QualityCertificateRepository certificates;
    private final CertificationAccess access;
    private final String publicBaseUrl;

    public CertificationWorkspaceController(
            HarvestBatchRepository batches,
            QualityCertificateRepository certificates,
            CertificationAccess access,
            @Value("${sumaqagro.certification.public-base-url}") String publicBaseUrl) {
        this.batches = batches;
        this.certificates = certificates;
        this.access = access;
        this.publicBaseUrl = publicBaseUrl;
    }

    @GetMapping(value = "/harvest-batches", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<BatchEntry>> list(@RequestParam Long cooperativeId,
                                                  @AuthenticationPrincipal UserDetailsImpl principal) {
        if (cooperativeId <= 0) return ResponseEntity.badRequest().build();
        if (principal == null) return ResponseEntity.status(401).build();
        List<HarvestBatch> visible;
        if (access.directs(principal, cooperativeId)) {
            visible = batches.findByCooperativeId(cooperativeId);
        } else {
            var memberIds = access.memberIds(principal, cooperativeId);
            if (memberIds.isEmpty()) return ResponseEntity.status(403).build();
            visible = batches.findByCooperativeIdAndMemberIdIn(cooperativeId, memberIds);
        }
        return ResponseEntity.ok(visible.stream().map(batch -> new BatchEntry(
                HarvestBatchResourceFromEntityAssembler.toResourceFromEntity(batch),
                certificates.findByHarvestBatchId(batch.getId())
                        .map(QualityCertificateResourceFromEntityAssembler::toResourceFromEntity)
                        .orElse(null))).toList());
    }

    @GetMapping(value = "/quality-certificates/document", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> download(@RequestParam String certificateNumber,
                                           @AuthenticationPrincipal UserDetailsImpl principal) {
        if (principal == null) return ResponseEntity.status(401).build();
        var certificate = certificates.findByCertificateNumber(certificateNumber).orElse(null);
        if (certificate == null) return ResponseEntity.notFound().build();
        var batch = batches.findById(certificate.getHarvestBatchId()).orElse(null);
        if (batch == null || !access.canRead(principal, batch)) {
            return ResponseEntity.notFound().build();
        }
        if (certificate.getStatus() != CertificateStatus.ACTIVE) return ResponseEntity.status(409).build();

        return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF).body(
                PdfQrGeneratorAssembler.renderIssued(batch, certificate.getCertificateNumber(),
                        certificate.getIssuedByUserId(), certificate.getIssuedAt(),
                        certificate.getCertificateHash(), publicBaseUrl));
    }

    public record BatchEntry(HarvestBatchResource batch, QualityCertificateResource certificate) {}
}
