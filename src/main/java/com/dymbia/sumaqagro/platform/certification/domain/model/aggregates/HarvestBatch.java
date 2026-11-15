package com.dymbia.sumaqagro.platform.certification.domain.model.aggregates;

import com.dymbia.sumaqagro.platform.certification.domain.model.events.BatchGradedEvent;
import com.dymbia.sumaqagro.platform.certification.domain.model.events.DeliveredBatchWeighedEvent;
import com.dymbia.sumaqagro.platform.certification.domain.model.valueobjects.BatchStatus;
import com.dymbia.sumaqagro.platform.certification.domain.model.valueobjects.CoffeeCuppingScores;
import com.dymbia.sumaqagro.platform.certification.domain.model.valueobjects.CropType;
import com.dymbia.sumaqagro.platform.certification.domain.model.valueobjects.Percentage;
import com.dymbia.sumaqagro.platform.certification.domain.model.valueobjects.PotatoCaliberDistribution;
import com.dymbia.sumaqagro.platform.certification.domain.model.valueobjects.QualityCategory;
import com.dymbia.sumaqagro.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Harvest batch aggregate root.
 * <p>
 * A lot received at the cooperative collection center. It starts pending qualification.
 * </p>
 * <p>
 * It is a plain domain class: the JPA mapping belongs to the infrastructure layer.
 * </p>
 *
 * @since 1.0
 */
@Getter
public class HarvestBatch extends AbstractDomainAggregateRoot<HarvestBatch> {

    private static final BigDecimal PEST_DAMAGE_THRESHOLD = BigDecimal.valueOf(15);
    private static final BigDecimal SPECIALTY_THRESHOLD = BigDecimal.valueOf(80);
    private static final BigDecimal MINIMUM_COFFEE_SAMPLE_KG = BigDecimal.ONE;

    private final Long id;
    private final String batchCode;
    private final Long cooperativeId;
    private final Long memberId;
    private final Long plotId;
    private final CropType cropType;
    private final LocalDate harvestedOn;
    private final Instant registeredAt;
    private BatchStatus status;
    private BigDecimal netWeightKg;
    private String sampleIdentifier;
    private BigDecimal sampleWeightKg;
    private BigDecimal firstCaliberPercentage;
    private BigDecimal secondCaliberPercentage;
    private BigDecimal thirdCaliberPercentage;
    private BigDecimal pestDamagePercentage;
    private BigDecimal coffeeCuppingScore;
    private boolean coffeeFermentDefect;
    private BigDecimal qualityScore;
    private QualityCategory qualityCategory;

    private HarvestBatch(Long id, String batchCode, Long cooperativeId, Long memberId, Long plotId, CropType cropType,
                         LocalDate harvestedOn, Instant registeredAt, BatchStatus status, BigDecimal netWeightKg,
                         String sampleIdentifier, BigDecimal sampleWeightKg, BigDecimal firstCaliberPercentage,
                         BigDecimal secondCaliberPercentage, BigDecimal thirdCaliberPercentage,
                         BigDecimal pestDamagePercentage, BigDecimal coffeeCuppingScore, boolean coffeeFermentDefect,
                         BigDecimal qualityScore, QualityCategory qualityCategory) {
        if (batchCode == null || batchCode.isBlank() || batchCode.trim().length() > 40) {
            throw new IllegalArgumentException("Batch code is required and must not exceed 40 characters");
        }
        this.id = id;
        this.batchCode = batchCode.trim();
        this.cooperativeId = requirePositiveId(cooperativeId, "Cooperative");
        this.memberId = requirePositiveId(memberId, "Member");
        this.plotId = requirePositiveId(plotId, "Plot");
        this.cropType = Objects.requireNonNull(cropType, "Crop type is required");
        this.harvestedOn = Objects.requireNonNull(harvestedOn, "Harvest date is required");
        this.registeredAt = Objects.requireNonNull(registeredAt, "Registration time is required");
        this.status = Objects.requireNonNull(status, "Batch status is required");
        this.netWeightKg = netWeightKg;
        this.sampleIdentifier = sampleIdentifier;
        this.sampleWeightKg = sampleWeightKg;
        this.firstCaliberPercentage = firstCaliberPercentage;
        this.secondCaliberPercentage = secondCaliberPercentage;
        this.thirdCaliberPercentage = thirdCaliberPercentage;
        this.pestDamagePercentage = pestDamagePercentage;
        this.coffeeCuppingScore = coffeeCuppingScore;
        this.coffeeFermentDefect = coffeeFermentDefect;
        this.qualityScore = qualityScore;
        this.qualityCategory = qualityCategory;
    }

    /**
     * Registers a batch delivered by a cooperative member.
     *
     * @param batchCode unique batch code
     * @param cooperativeId cooperative that receives the batch
     * @param memberId delivering member
     * @param plotId source plot
     * @param cropType evaluated crop
     * @param harvestedOn harvest date
     */
    public HarvestBatch(String batchCode, Long cooperativeId, Long memberId, Long plotId, CropType cropType,
                        LocalDate harvestedOn) {
        this(null, batchCode, cooperativeId, memberId, plotId, cropType, harvestedOn, Instant.now(),
                BatchStatus.PENDING, null, null, null, null, null, null, null, null, false, null, null);
    }

    /**
     * Rebuilds a harvest batch that already exists in the database.
     */
    public static HarvestBatch reconstitute(Long id, String batchCode, Long cooperativeId, Long memberId, Long plotId,
                                            CropType cropType, LocalDate harvestedOn, Instant registeredAt,
                                            BatchStatus status, BigDecimal netWeightKg, String sampleIdentifier,
                                            BigDecimal sampleWeightKg, BigDecimal firstCaliberPercentage,
                                            BigDecimal secondCaliberPercentage, BigDecimal thirdCaliberPercentage,
                                            BigDecimal pestDamagePercentage, BigDecimal coffeeCuppingScore,
                                            boolean coffeeFermentDefect, BigDecimal qualityScore,
                                            QualityCategory qualityCategory) {
        return new HarvestBatch(id, batchCode, cooperativeId, memberId, plotId, cropType, harvestedOn, registeredAt,
                status, netWeightKg, sampleIdentifier, sampleWeightKg, firstCaliberPercentage,
                secondCaliberPercentage, thirdCaliberPercentage, pestDamagePercentage, coffeeCuppingScore,
                coffeeFermentDefect, qualityScore, qualityCategory);
    }

    /**
     * Records the net weight delivered at the collection center.
     *
     * @param netWeightKg positive net weight in kilograms
     */
    public void weigh(BigDecimal netWeightKg) {
        requireStatus(BatchStatus.PENDING, "Only a pending batch can be weighed");
        if (this.netWeightKg != null) {
            throw new IllegalStateException("The batch has already been weighed");
        }
        if (netWeightKg == null || netWeightKg.signum() <= 0) {
            throw new IllegalArgumentException("Batch weight must be greater than zero");
        }
        this.netWeightKg = netWeightKg;
        registerDomainEvent(new DeliveredBatchWeighedEvent(id, plotId, netWeightKg));
    }

    /**
     * Records the net weight from the gross reading minus the packaging tare.
     *
     * @param grossWeightKg total weighed mass, including packaging
     * @param tareKg packaging mass to subtract
     */
    public void weigh(BigDecimal grossWeightKg, BigDecimal tareKg) {
        if (grossWeightKg == null || tareKg == null || tareKg.signum() < 0) {
            throw new IllegalArgumentException("Gross weight and a non-negative tare are required");
        }
        weigh(grossWeightKg.subtract(tareKg));
    }

    /**
     * Attaches the representative sample used for grading or cupping.
     *
     * @param identifier sample identifier
     * @param weightKg sample mass in kilograms
     */
    public void extractSample(String identifier, BigDecimal weightKg) {
        requireStatus(BatchStatus.PENDING, "Only a pending batch can be sampled");
        if (netWeightKg == null) {
            throw new IllegalStateException("A batch must be weighed before sampling");
        }
        if (sampleIdentifier != null) {
            throw new IllegalStateException("The batch already has a representative sample");
        }
        if (identifier == null || identifier.isBlank()) {
            throw new IllegalArgumentException("Sample identifier is required");
        }
        if (weightKg == null || weightKg.signum() <= 0 || weightKg.compareTo(netWeightKg) > 0) {
            throw new IllegalArgumentException("Sample weight must be greater than zero and within the batch weight");
        }
        if (cropType == CropType.SPECIALTY_COFFEE && weightKg.compareTo(MINIMUM_COFFEE_SAMPLE_KG) < 0) {
            throw new IllegalArgumentException("Coffee cupping requires a sample of at least 1 kg");
        }
        this.sampleIdentifier = identifier.trim();
        this.sampleWeightKg = weightKg;
    }

    /**
     * Records the potato calibers and closes the pending qualification.
     */
    public void gradePotato(PotatoCaliberDistribution distribution) {
        requireReadyForGrading();
        if (cropType != CropType.ANDEAN_POTATO) {
            throw new IllegalStateException("Potato calibers can only be recorded for potato batches");
        }
        Objects.requireNonNull(distribution, "Potato caliber distribution is required");
        this.firstCaliberPercentage = distribution.firstPercentage().value();
        this.secondCaliberPercentage = distribution.secondPercentage().value();
        this.thirdCaliberPercentage = distribution.thirdPercentage().value();
        this.pestDamagePercentage = distribution.pestDamagePercentage().value();
        this.status = BatchStatus.GRADED;
        registerDomainEvent(new BatchGradedEvent(id, cropType));
    }

    /**
     * Records the coffee cupping and closes the pending qualification.
     */
    public void cupCoffee(CoffeeCuppingScores scores, boolean fermentDefect) {
        requireReadyForGrading();
        if (cropType != CropType.SPECIALTY_COFFEE) {
            throw new IllegalStateException("Coffee cupping can only be recorded for coffee batches");
        }
        Objects.requireNonNull(scores, "Coffee cupping scores are required");
        this.coffeeCuppingScore = scores.totalScore().value();
        this.coffeeFermentDefect = fermentDefect;
        this.status = BatchStatus.GRADED;
        registerDomainEvent(new BatchGradedEvent(id, cropType));
    }

    /**
     * Assigns the quality score after potato grading or coffee cupping.
     *
     * @param score score from 0 to 100
     * @param category resulting category
     */
    public void assignQualityScore(BigDecimal score, QualityCategory category) {
        requireStatus(BatchStatus.GRADED, "A batch must be graded before assigning its quality score");
        if (qualityCategory != null) {
            throw new IllegalStateException("The quality score is already assigned");
        }
        Percentage validatedScore = new Percentage(score);
        Objects.requireNonNull(category, "Quality category is required");
        if (cropType == CropType.ANDEAN_POTATO) {
            requirePotatoCategory(category);
        } else if (coffeeCuppingScore == null || category != coffeeCategory()) {
            throw new IllegalArgumentException("The quality category does not match the coffee cupping result");
        }
        this.qualityScore = validatedScore.value();
        this.qualityCategory = category;
    }

    /**
     * Marks the batch as certified once its quality category is assigned.
     */
    public void markCertified() {
        if (status != BatchStatus.GRADED || qualityCategory == null) {
            throw new IllegalStateException("A batch needs an assigned quality category before it can be certified");
        }
        this.status = BatchStatus.CERTIFIED;
    }

    /**
     * @return {@code true} when a certificate can be issued
     */
    public boolean isReadyForCertification() {
        return status == BatchStatus.GRADED && qualityScore != null && qualityCategory != null;
    }

    private QualityCategory coffeeCategory() {
        boolean specialty = !coffeeFermentDefect && coffeeCuppingScore.compareTo(SPECIALTY_THRESHOLD) >= 0;
        return specialty ? QualityCategory.SPECIALTY_COFFEE : QualityCategory.COMMERCIAL_COFFEE;
    }

    private void requirePotatoCategory(QualityCategory category) {
        if (firstCaliberPercentage == null) {
            throw new IllegalStateException("Potato caliber grading is required before assigning the quality score");
        }
        if (!category.isPotatoCategory()) {
            throw new IllegalArgumentException("The selected quality category does not apply to potato");
        }
        if (pestDamagePercentage.compareTo(PEST_DAMAGE_THRESHOLD) > 0
                && category != QualityCategory.B_GRADE_REQUIRES_SORTING) {
            throw new IllegalArgumentException("Pest damage above 15% requires manual sorting");
        }
    }

    private void requireReadyForGrading() {
        requireStatus(BatchStatus.PENDING, "Only a pending batch can be graded");
        if (netWeightKg == null || sampleIdentifier == null) {
            throw new IllegalStateException("A weighed batch and a representative sample are required before grading");
        }
    }

    private void requireStatus(BatchStatus expected, String message) {
        if (status != expected) {
            throw new IllegalStateException(message);
        }
    }

    private static Long requirePositiveId(Long value, String label) {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException(label + " id must be greater than zero");
        }
        return value;
    }

}
