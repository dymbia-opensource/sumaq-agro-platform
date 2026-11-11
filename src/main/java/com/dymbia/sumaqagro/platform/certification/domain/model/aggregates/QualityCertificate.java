package com.dymbia.sumaqagro.platform.certification.domain.model.aggregates;

import com.dymbia.sumaqagro.platform.certification.domain.model.valueobjects.CertificateStatus;
import com.dymbia.sumaqagro.platform.certification.domain.model.valueobjects.QualityCategory;
import com.dymbia.sumaqagro.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Quality certificate aggregate root.
 * <p>
 * One certificate corresponds to one harvest batch. Its hash seals the issued content.
 * </p>
 * <p>
 * It is a plain domain class: the JPA mapping belongs to the infrastructure layer.
 * </p>
 *
 * @since 1.0
 */
@Getter
public class QualityCertificate extends AbstractDomainAggregateRoot<QualityCertificate> {

    private static final Pattern SHA_256 = Pattern.compile("^[0-9a-fA-F]{64}$");

    private final Long id;
    private final String certificateNumber;
    private final Long harvestBatchId;
    private final QualityCategory qualityCategory;
    private final String certificateHash;
    private final Long issuedByUserId;
    private final Instant issuedAt;
    private CertificateStatus status;

    private QualityCertificate(Long id, String certificateNumber, Long harvestBatchId, QualityCategory qualityCategory,
                               String certificateHash, Long issuedByUserId, Instant issuedAt, CertificateStatus status) {
        if (certificateNumber == null || certificateNumber.isBlank()) {
            throw new IllegalArgumentException("Certificate number is required");
        }
        this.id = id;
        this.certificateNumber = certificateNumber.trim();
        this.harvestBatchId = requirePositiveId(harvestBatchId);
        this.qualityCategory = Objects.requireNonNull(qualityCategory, "Quality category is required");
        this.certificateHash = normalizeHash(certificateHash);
        this.issuedByUserId = requirePositiveId(issuedByUserId);
        this.issuedAt = Objects.requireNonNull(issuedAt, "Certificate issue time is required");
        this.status = Objects.requireNonNull(status, "Certificate status is required");
    }

    /**
     * Issues an active certificate for one harvest batch.
     *
     * @param certificateNumber public certificate number
     * @param harvestBatchId certified batch
     * @param qualityCategory sealed quality category
     * @param certificateHash SHA-256 hash of the certificate content
     * @param issuedByUserId issuing user
     * @param issuedAt issuance time
     */
    public QualityCertificate(String certificateNumber, Long harvestBatchId, QualityCategory qualityCategory,
                              String certificateHash, Long issuedByUserId, Instant issuedAt) {
        this(null, certificateNumber, harvestBatchId, qualityCategory, certificateHash, issuedByUserId, issuedAt,
                CertificateStatus.ACTIVE);
    }

    /**
     * Rebuilds a certificate that already exists in the database.
     */
    public static QualityCertificate reconstitute(Long id, String certificateNumber, Long harvestBatchId,
                                                  QualityCategory qualityCategory, String certificateHash,
                                                  Long issuedByUserId, Instant issuedAt, CertificateStatus status) {
        return new QualityCertificate(id, certificateNumber, harvestBatchId, qualityCategory, certificateHash,
                issuedByUserId, issuedAt, status);
    }

    /**
     * Compares the presented hash with this active certificate.
     *
     * @param presentedHash hash supplied with the certificate number
     * @return {@code true} when the certificate is active and the hash matches
     */
    public boolean verify(String presentedHash) {
        if (status != CertificateStatus.ACTIVE || presentedHash == null) {
            return false;
        }
        try {
            String presented = normalizeHash(presentedHash);
            return MessageDigest.isEqual(certificateHash.getBytes(StandardCharsets.US_ASCII),
                    presented.getBytes(StandardCharsets.US_ASCII));
        } catch (IllegalArgumentException exception) {
            return false;
        }
    }

    /**
     * Revokes the certificate so public verification no longer succeeds.
     */
    public void revoke() {
        if (status != CertificateStatus.ACTIVE) {
            throw new IllegalStateException("Only an active certificate can be revoked");
        }
        this.status = CertificateStatus.REVOKED;
    }

    private static Long requirePositiveId(Long value) {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("Identifier must be greater than zero");
        }
        return value;
    }

    private static String normalizeHash(String hash) {
        if (hash == null || !SHA_256.matcher(hash.trim()).matches()) {
            throw new IllegalArgumentException("Certificate hash must contain exactly 64 hexadecimal characters");
        }
        return hash.trim().toLowerCase(Locale.ROOT);
    }
}
