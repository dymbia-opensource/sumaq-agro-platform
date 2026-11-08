package com.dymbia.sumaqagro.platform.certification.application.internal.commandservices;

import com.dymbia.sumaqagro.platform.certification.application.acl.ExternalProfilesService;
import com.dymbia.sumaqagro.platform.certification.application.commandservices.HarvestBatchCommandService;
import com.dymbia.sumaqagro.platform.certification.domain.model.aggregates.HarvestBatch;
import com.dymbia.sumaqagro.platform.certification.domain.model.commands.CupCoffeeCommand;
import com.dymbia.sumaqagro.platform.certification.domain.model.commands.ExtractRepresentativeSampleCommand;
import com.dymbia.sumaqagro.platform.certification.domain.model.commands.GradePotatoCalibersCommand;
import com.dymbia.sumaqagro.platform.certification.domain.model.commands.RegisterHarvestBatchCommand;
import com.dymbia.sumaqagro.platform.certification.domain.model.commands.WeighHarvestBatchCommand;
import com.dymbia.sumaqagro.platform.certification.domain.repositories.HarvestBatchRepository;
import com.dymbia.sumaqagro.platform.shared.application.result.ApplicationError;
import com.dymbia.sumaqagro.platform.shared.application.result.Result;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Consumer;

/**
 * Changes harvest batches and checks the rules that need another context or the database.
 * Saving the batch publishes the weighed and graded domain events.
 *
 * @since 1.0
 */
@Service
public class HarvestBatchCommandServiceImpl implements HarvestBatchCommandService {

    private static final String DUPLICATE_BATCH_CODE = "A batch with this code already exists.";

    private final HarvestBatchRepository harvestBatchRepository;
    private final ExternalProfilesService externalProfilesService;

    public HarvestBatchCommandServiceImpl(HarvestBatchRepository harvestBatchRepository,
                                          ExternalProfilesService externalProfilesService) {
        this.harvestBatchRepository = harvestBatchRepository;
        this.externalProfilesService = externalProfilesService;
    }

    @Override
    @Transactional
    public Result<HarvestBatch, ApplicationError> handle(RegisterHarvestBatchCommand command) {
        if (!externalProfilesService.memberBelongsToCooperative(command.cooperativeId(), command.memberId())) {
            return Result.failure(ApplicationError.businessRuleViolation(
                    "member-of-cooperative", "The member does not belong to the cooperative."));
        }
        if (harvestBatchRepository.existsByBatchCode(command.batchCode())) {
            return Result.failure(ApplicationError.conflict("HarvestBatch", DUPLICATE_BATCH_CODE));
        }
        try {
            HarvestBatch batch = harvestBatchRepository.save(new HarvestBatch(
                    command.batchCode(),
                    command.cooperativeId(),
                    command.memberId(),
                    command.plotId(),
                    command.cropType(),
                    command.harvestedOn()));
            return Result.success(batch);
        } catch (DataIntegrityViolationException exception) {
            return Result.failure(ApplicationError.conflict("HarvestBatch", DUPLICATE_BATCH_CODE));
        }
    }

    @Override
    @Transactional
    public Result<HarvestBatch, ApplicationError> handle(WeighHarvestBatchCommand command) {
        return update(command.harvestBatchId(), batch -> batch.weigh(command.grossWeightKg(), command.tareKg()));
    }

    @Override
    @Transactional
    public Result<HarvestBatch, ApplicationError> handle(ExtractRepresentativeSampleCommand command) {
        return update(command.harvestBatchId(),
                batch -> batch.extractSample(command.sampleIdentifier(), command.sampleWeightKg()));
    }

    @Override
    @Transactional
    public Result<HarvestBatch, ApplicationError> handle(GradePotatoCalibersCommand command) {
        return update(command.harvestBatchId(), batch -> batch.gradePotato(
                command.firstPercentage(),
                command.secondPercentage(),
                command.thirdPercentage(),
                command.pestDamagePercentage()));
    }

    @Override
    @Transactional
    public Result<HarvestBatch, ApplicationError> handle(CupCoffeeCommand command) {
        return update(command.harvestBatchId(), batch -> batch.cupCoffee(
                command.aroma(),
                command.flavor(),
                command.acidity(),
                command.body(),
                command.totalScore(),
                command.fermentDefect()));
    }

    private Result<HarvestBatch, ApplicationError> update(Long harvestBatchId, Consumer<HarvestBatch> action) {
        return harvestBatchRepository.findById(harvestBatchId)
                .map(batch -> applyAndSave(batch, action))
                .orElseGet(() -> Result.failure(ApplicationError.notFound("HarvestBatch", String.valueOf(harvestBatchId))));
    }

    private Result<HarvestBatch, ApplicationError> applyAndSave(HarvestBatch batch, Consumer<HarvestBatch> action) {
        try {
            action.accept(batch);
            return Result.success(harvestBatchRepository.save(batch));
        } catch (IllegalStateException | IllegalArgumentException exception) {
            return Result.failure(ApplicationError.businessRuleViolation("harvest-batch", exception.getMessage()));
        } catch (DataIntegrityViolationException exception) {
            return Result.failure(ApplicationError.conflict("HarvestBatch", "The batch could not be saved."));
        }
    }
}
