package com.dymbia.sumaqagro.platform.certification.interfaces.rest.resources;

import java.math.BigDecimal;

/**
 * Public traceability returned when a visitor verifies a certificate.
 */
public record PublicTraceabilityResource(
        String certificateNumber,
        String status,
        boolean authentic,
        String batchCode,
        String cropType,
        String qualityCategory,
        BigDecimal qualityScore,
        String plotName,
        String region,
        String seedVariety
) {
}
