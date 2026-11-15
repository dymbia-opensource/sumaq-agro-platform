package com.dymbia.sumaqagro.platform.certification.application.internal.eventhandlers;

import com.dymbia.sumaqagro.platform.certification.domain.model.aggregates.HarvestBatch;
import com.dymbia.sumaqagro.platform.certification.domain.model.events.BatchGradedEvent;
import com.dymbia.sumaqagro.platform.certification.domain.repositories.HarvestBatchRepository;
import com.dymbia.sumaqagro.platform.certification.domain.model.valueobjects.BatchStatus;
import com.dymbia.sumaqagro.platform.certification.domain.model.valueobjects.CropType;
import com.dymbia.sumaqagro.platform.certification.domain.model.valueobjects.QualityCategory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.math.BigDecimal;

/**
 * Assigns the quality score when potato grading or coffee cupping has been saved.
 *
 * @since 1.0
 */
@Component
public class BatchGradedEventHandler {

    private static final BigDecimal PREMIUM_FIRST_CALIBER = BigDecimal.valueOf(50);
    private static final BigDecimal PEST_DAMAGE_THRESHOLD = BigDecimal.valueOf(15);
    private static final BigDecimal SPECIALTY_THRESHOLD = BigDecimal.valueOf(80);

    private final HarvestBatchRepository harvestBatchRepository;

    public BatchGradedEventHandler(HarvestBatchRepository harvestBatchRepository) {
        this.harvestBatchRepository = harvestBatchRepository;
    }

    /**
     * Loads the graded batch and stores its quality score before the transaction commits.
     *
     * @param event grading finished for one batch
     */
    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void on(BatchGradedEvent event) {
        if (event.harvestBatchId() == null) {
            return;
        }
        harvestBatchRepository.findById(event.harvestBatchId()).ifPresent(this::assignQualityScore);
    }

    private void assignQualityScore(HarvestBatch batch) {
        if (batch.getQualityCategory() != null || batch.getStatus() != BatchStatus.GRADED) {
            return;
        }
        if (batch.getCropType() == CropType.ANDEAN_POTATO) {
            batch.assignQualityScore(batch.getFirstCaliberPercentage(), potatoCategory(batch));
        } else {
            batch.assignQualityScore(batch.getCoffeeCuppingScore(), coffeeCategory(batch));
        }
        harvestBatchRepository.save(batch);
    }

    private static QualityCategory potatoCategory(HarvestBatch batch) {
        if (batch.getPestDamagePercentage().compareTo(PEST_DAMAGE_THRESHOLD) > 0) {
            return QualityCategory.B_GRADE_REQUIRES_SORTING;
        }
        if (batch.getFirstCaliberPercentage().compareTo(PREMIUM_FIRST_CALIBER) >= 0) {
            return QualityCategory.PREMIUM_GOLD;
        }
        return QualityCategory.STANDARD;
    }

    private static QualityCategory coffeeCategory(HarvestBatch batch) {
        boolean specialty = !batch.isCoffeeFermentDefect()
                && batch.getCoffeeCuppingScore().compareTo(SPECIALTY_THRESHOLD) >= 0;
        return specialty
                ? QualityCategory.SPECIALTY_COFFEE
                : QualityCategory.COMMERCIAL_COFFEE;
    }
}
