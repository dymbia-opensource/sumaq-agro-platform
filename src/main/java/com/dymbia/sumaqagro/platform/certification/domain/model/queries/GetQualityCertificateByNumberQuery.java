package com.dymbia.sumaqagro.platform.certification.domain.model.queries;

/**
 * Query to retrieve a quality certificate by its public number.
 *
 * @param certificateNumber public certificate number
 * @since 1.0
 */
public record GetQualityCertificateByNumberQuery(String certificateNumber) {

    public GetQualityCertificateByNumberQuery {
        if (certificateNumber == null || certificateNumber.isBlank()) {
            throw new IllegalArgumentException("Certificate number is required");
        }
    }
}
