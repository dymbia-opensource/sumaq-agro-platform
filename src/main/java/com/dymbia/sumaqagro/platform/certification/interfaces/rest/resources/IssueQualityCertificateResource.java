package com.dymbia.sumaqagro.platform.certification.interfaces.rest.resources;

/**
 * Request body used to issue a quality certificate.
 */
public record IssueQualityCertificateResource(
        String certificateNumber,
        Long harvestBatchId,
        Long issuedByUserId
) {
}
