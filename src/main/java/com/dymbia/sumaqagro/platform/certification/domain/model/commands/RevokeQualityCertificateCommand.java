package com.dymbia.sumaqagro.platform.certification.domain.model.commands;

/**
 * Command to revoke an active quality certificate.
 *
 * @param qualityCertificateId identity of the certificate
 * @since 1.0
 */
public record RevokeQualityCertificateCommand(Long qualityCertificateId) {

    public RevokeQualityCertificateCommand {
        if (qualityCertificateId == null || qualityCertificateId <= 0) {
            throw new IllegalArgumentException("Quality certificate id must be greater than zero");
        }
    }
}
