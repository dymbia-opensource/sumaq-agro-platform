package com.dymbia.sumaqagro.platform.certification.domain.model.commands;

import com.dymbia.sumaqagro.platform.certification.domain.model.valueobjects.QualityCategory;

import java.time.Instant;
import java.util.regex.Pattern;

/**
 * Command to issue a quality certificate for one harvest batch.
 *
 * @param certificateNumber public certificate number
 * @param harvestBatchId    certified batch
 * @param qualityCategory   category sealed in the certificate
 * @param certificateHash   SHA-256 hash of the certificate content
 * @param issuedByUserId    user who issues the certificate
 * @param issuedAt          issuance time
 * @since 1.0
 */
public record IssueQualityCertificateCommand(
        String certificateNumber,
        Long harvestBatchId,
        QualityCategory qualityCategory,
        String certificateHash,
        Long issuedByUserId,
        Instant issuedAt
) {
    private static final Pattern SHA_256 = Pattern.compile("^[0-9a-fA-F]{64}$");

    public IssueQualityCertificateCommand {
        if (certificateNumber == null || certificateNumber.isBlank()) {
            throw new IllegalArgumentException("Certificate number is required");
        }
        if (harvestBatchId == null || harvestBatchId <= 0) {
            throw new IllegalArgumentException("Harvest batch id must be greater than zero");
        }
        if (qualityCategory == null) {
            throw new IllegalArgumentException("Quality category is required");
        }
        if (certificateHash == null || !SHA_256.matcher(certificateHash.trim()).matches()) {
            throw new IllegalArgumentException("Certificate hash must contain exactly 64 hexadecimal characters");
        }
        if (issuedByUserId == null || issuedByUserId <= 0) {
            throw new IllegalArgumentException("Issuer id must be greater than zero");
        }
        if (issuedAt == null) {
            throw new IllegalArgumentException("Certificate issue time is required");
        }
    }
}
