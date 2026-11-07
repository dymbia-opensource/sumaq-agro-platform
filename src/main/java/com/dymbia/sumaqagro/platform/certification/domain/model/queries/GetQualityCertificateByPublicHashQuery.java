package com.dymbia.sumaqagro.platform.certification.domain.model.queries;

import java.util.regex.Pattern;

/**
 * Query to verify a quality certificate with its public number and hash.
 *
 * @param certificateNumber public certificate number
 * @param certificateHash   SHA-256 hash presented by the visitor
 * @since 1.0
 */
public record GetQualityCertificateByPublicHashQuery(String certificateNumber, String certificateHash) {

    private static final Pattern SHA_256 = Pattern.compile("^[0-9a-fA-F]{64}$");

    public GetQualityCertificateByPublicHashQuery {
        if (certificateNumber == null || certificateNumber.isBlank()) {
            throw new IllegalArgumentException("Certificate number is required");
        }
        if (certificateHash == null || !SHA_256.matcher(certificateHash.trim()).matches()) {
            throw new IllegalArgumentException("Certificate hash must contain exactly 64 hexadecimal characters");
        }
    }
}
