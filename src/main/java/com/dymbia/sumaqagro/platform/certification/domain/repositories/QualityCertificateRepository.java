package com.dymbia.sumaqagro.platform.certification.domain.repositories;

import com.dymbia.sumaqagro.platform.certification.domain.model.aggregates.QualityCertificate;

import java.util.Optional;

/**
 * Repository port of the quality certificate aggregate.
 * The domain says what it needs and the infrastructure layer implements it.
 *
 * @since 1.0
 */
public interface QualityCertificateRepository {

    /**
     * Finds a quality certificate by its id.
     *
     * @param id the certificate id
     * @return the certificate, or an empty optional
     */
    Optional<QualityCertificate> findById(Long id);

    /**
     * Finds a quality certificate by its public number.
     *
     * @param certificateNumber the certificate number
     * @return the certificate, or an empty optional
     */
    Optional<QualityCertificate> findByCertificateNumber(String certificateNumber);

    /**
     * Finds the certificate issued for a harvest batch.
     *
     * @param harvestBatchId the batch id
     * @return the certificate, or an empty optional when the batch has none
     */
    Optional<QualityCertificate> findByHarvestBatchId(Long harvestBatchId);

    /**
     * Checks whether a certificate number is already in use.
     *
     * @param certificateNumber the certificate number
     * @return {@code true} when the number already belongs to a certificate
     */
    boolean existsByCertificateNumber(String certificateNumber);

    /**
     * Checks whether a harvest batch already has a certificate.
     *
     * @param harvestBatchId the batch id
     * @return {@code true} when the batch already has a certificate
     */
    boolean existsByHarvestBatchId(Long harvestBatchId);

    /**
     * Saves the quality certificate.
     *
     * @param qualityCertificate the certificate to save
     * @return the saved certificate, with its id
     */
    QualityCertificate save(QualityCertificate qualityCertificate);
}
