package com.dymbia.sumaqagro.platform.certification.domain.model.queries;

/**
 * Query for a quality certificate by its id.
 *
 * @param qualityCertificateId the certificate id
 * @since 1.1
 */
public record GetQualityCertificateByIdQuery(Long qualityCertificateId) {

    public GetQualityCertificateByIdQuery {
        if (qualityCertificateId == null || qualityCertificateId <= 0) {
            throw new IllegalArgumentException("Quality certificate id must be greater than zero");
        }
    }
}
