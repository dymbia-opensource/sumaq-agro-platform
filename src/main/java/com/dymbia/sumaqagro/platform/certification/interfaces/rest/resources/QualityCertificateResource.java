package com.dymbia.sumaqagro.platform.certification.interfaces.rest.resources;

import java.time.Instant;

/**
 * Quality certificate returned by the API.
 */
public record QualityCertificateResource(
        Long id,
        String certificateNumber,
        Long harvestBatchId,
        String qualityCategory,
        String certificateHash,
        Long issuedByUserId,
        Instant issuedAt,
        String status
) {
}
