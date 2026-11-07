package com.dymbia.sumaqagro.platform.certification.infrastructure.persistence.jpa.entities;

import com.dymbia.sumaqagro.platform.certification.domain.model.aggregates.HarvestBatch.QualityCategory;
import com.dymbia.sumaqagro.platform.certification.domain.model.aggregates.QualityCertificate.CertificateStatus;
import com.dymbia.sumaqagro.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

/**
 * JPA persistence entity for quality certificates.
 */
@Entity
@Table(name = "quality_certificates", uniqueConstraints = {
        @UniqueConstraint(
                name = QualityCertificatePersistenceEntity.CERTIFICATE_NUMBER_UNIQUE_CONSTRAINT,
                columnNames = "certificate_number"),
        @UniqueConstraint(
                name = QualityCertificatePersistenceEntity.HARVEST_BATCH_UNIQUE_CONSTRAINT,
                columnNames = "harvest_batch_id")
})
@Getter
@Setter
@NoArgsConstructor
public class QualityCertificatePersistenceEntity extends AuditableAbstractPersistenceEntity {

    public static final String CERTIFICATE_NUMBER_UNIQUE_CONSTRAINT = "uk_quality_certificates_certificate_number";
    public static final String HARVEST_BATCH_UNIQUE_CONSTRAINT = "uk_quality_certificates_harvest_batch_id";

    @Column(name = "certificate_number", nullable = false, length = 40)
    private String certificateNumber;

    @Column(name = "harvest_batch_id", nullable = false)
    private Long harvestBatchId;

    @Enumerated(EnumType.STRING)
    @Column(name = "quality_category", nullable = false, length = 30)
    private QualityCategory qualityCategory;

    @Column(name = "certificate_hash", nullable = false, length = 64)
    private String certificateHash;

    @Column(name = "issued_by_user_id", nullable = false)
    private Long issuedByUserId;

    @Column(name = "issued_at", nullable = false)
    private Instant issuedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private CertificateStatus status;
}
