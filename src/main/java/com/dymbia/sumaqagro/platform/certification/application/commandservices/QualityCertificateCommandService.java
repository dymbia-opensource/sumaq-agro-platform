package com.dymbia.sumaqagro.platform.certification.application.commandservices;

import com.dymbia.sumaqagro.platform.certification.domain.model.aggregates.QualityCertificate;
import com.dymbia.sumaqagro.platform.certification.domain.model.commands.IssueQualityCertificateCommand;
import com.dymbia.sumaqagro.platform.certification.domain.model.commands.RevokeQualityCertificateCommand;
import com.dymbia.sumaqagro.platform.shared.application.result.ApplicationError;
import com.dymbia.sumaqagro.platform.shared.application.result.Result;

/**
 * Use cases that issue and revoke a quality certificate.
 *
 * @since 1.0
 */
public interface QualityCertificateCommandService {

    /**
     * Issues the quality certificate of a graded harvest batch.
     *
     * @param command certificate number, batch, category, hash, issuer and issue time
     * @return the issued certificate
     */
    Result<QualityCertificate, ApplicationError> handle(IssueQualityCertificateCommand command);

    /**
     * Revokes an active quality certificate.
     *
     * @param command certificate id
     * @return the revoked certificate
     */
    Result<QualityCertificate, ApplicationError> handle(RevokeQualityCertificateCommand command);
}
