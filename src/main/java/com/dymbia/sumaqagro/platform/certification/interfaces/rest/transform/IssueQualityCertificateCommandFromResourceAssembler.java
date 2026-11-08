package com.dymbia.sumaqagro.platform.certification.interfaces.rest.transform;

import com.dymbia.sumaqagro.platform.certification.domain.model.aggregates.HarvestBatch;
import com.dymbia.sumaqagro.platform.certification.domain.model.commands.IssueQualityCertificateCommand;
import com.dymbia.sumaqagro.platform.certification.interfaces.rest.resources.IssueQualityCertificateResource;

import java.time.Instant;
import java.util.Objects;

public final class IssueQualityCertificateCommandFromResourceAssembler {

    private IssueQualityCertificateCommandFromResourceAssembler() {
    }

    public static IssueQualityCertificateCommand toCommandFromResource(
            IssueQualityCertificateResource resource,
            HarvestBatch.QualityCategory qualityCategory,
            String certificateHash,
            Instant issuedAt) {
        Objects.requireNonNull(resource, "Issue quality certificate request body is required.");
        return new IssueQualityCertificateCommand(
                resource.certificateNumber(),
                resource.harvestBatchId(),
                qualityCategory,
                certificateHash,
                resource.issuedByUserId(),
                issuedAt);
    }
}
