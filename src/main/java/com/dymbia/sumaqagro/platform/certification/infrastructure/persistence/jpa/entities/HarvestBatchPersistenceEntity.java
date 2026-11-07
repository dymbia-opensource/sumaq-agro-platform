package com.dymbia.sumaqagro.platform.certification.infrastructure.persistence.jpa.entities;

import com.dymbia.sumaqagro.platform.certification.domain.model.aggregates.HarvestBatch.BatchStatus;
import com.dymbia.sumaqagro.platform.certification.domain.model.aggregates.HarvestBatch.CropType;
import com.dymbia.sumaqagro.platform.certification.domain.model.aggregates.HarvestBatch.QualityCategory;
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

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

/**
 * JPA persistence entity for harvest batches.
 */
@Entity
@Table(name = "harvest_batches", uniqueConstraints = {
        @UniqueConstraint(
                name = HarvestBatchPersistenceEntity.BATCH_CODE_UNIQUE_CONSTRAINT,
                columnNames = "batch_code")
})
@Getter
@Setter
@NoArgsConstructor
public class HarvestBatchPersistenceEntity extends AuditableAbstractPersistenceEntity {

    public static final String BATCH_CODE_UNIQUE_CONSTRAINT = "uk_harvest_batches_batch_code";

    @Column(name = "batch_code", nullable = false, length = 40)
    private String batchCode;

    @Column(name = "cooperative_id", nullable = false)
    private Long cooperativeId;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "plot_id", nullable = false)
    private Long plotId;

    @Enumerated(EnumType.STRING)
    @Column(name = "crop_type", nullable = false, length = 30)
    private CropType cropType;

    @Column(name = "harvested_on", nullable = false)
    private LocalDate harvestedOn;

    @Column(name = "registered_at", nullable = false)
    private Instant registeredAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private BatchStatus status;

    @Column(name = "net_weight_kg", precision = 12, scale = 3)
    private BigDecimal netWeightKg;

    @Column(name = "sample_identifier", length = 40)
    private String sampleIdentifier;

    @Column(name = "sample_weight_kg", precision = 12, scale = 3)
    private BigDecimal sampleWeightKg;

    @Column(name = "first_caliber_percentage", precision = 5, scale = 2)
    private BigDecimal firstCaliberPercentage;

    @Column(name = "second_caliber_percentage", precision = 5, scale = 2)
    private BigDecimal secondCaliberPercentage;

    @Column(name = "third_caliber_percentage", precision = 5, scale = 2)
    private BigDecimal thirdCaliberPercentage;

    @Column(name = "pest_damage_percentage", precision = 5, scale = 2)
    private BigDecimal pestDamagePercentage;

    @Column(name = "coffee_cupping_score", precision = 5, scale = 2)
    private BigDecimal coffeeCuppingScore;

    @Column(name = "coffee_ferment_defect", nullable = false)
    private boolean coffeeFermentDefect;

    @Column(name = "quality_score", precision = 5, scale = 2)
    private BigDecimal qualityScore;

    @Enumerated(EnumType.STRING)
    @Column(name = "quality_category", length = 30)
    private QualityCategory qualityCategory;
}
