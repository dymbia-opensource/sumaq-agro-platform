package com.dymbia.sumaqagro.platform.certification.application.commandservices;

import com.dymbia.sumaqagro.platform.certification.domain.model.aggregates.HarvestBatch;
import com.dymbia.sumaqagro.platform.certification.domain.model.commands.CupCoffeeCommand;
import com.dymbia.sumaqagro.platform.certification.domain.model.commands.ExtractRepresentativeSampleCommand;
import com.dymbia.sumaqagro.platform.certification.domain.model.commands.GradePotatoCalibersCommand;
import com.dymbia.sumaqagro.platform.certification.domain.model.commands.RegisterHarvestBatchCommand;
import com.dymbia.sumaqagro.platform.certification.domain.model.commands.WeighHarvestBatchCommand;
import com.dymbia.sumaqagro.platform.shared.application.result.ApplicationError;
import com.dymbia.sumaqagro.platform.shared.application.result.Result;

/**
 * Use cases that change a harvest batch.
 *
 * @since 1.0
 */
public interface HarvestBatchCommandService {

    /**
     * Registers a harvest batch delivered by a cooperative member.
     *
     * @param command registration data
     * @return the new batch
     */
    Result<HarvestBatch, ApplicationError> handle(RegisterHarvestBatchCommand command);

    /**
     * Records the net weight of a registered batch.
     *
     * @param command gross weight and tare
     * @return the weighed batch
     */
    Result<HarvestBatch, ApplicationError> handle(WeighHarvestBatchCommand command);

    /**
     * Attaches the representative sample of a weighed batch.
     *
     * @param command sample identifier and weight
     * @return the sampled batch
     */
    Result<HarvestBatch, ApplicationError> handle(ExtractRepresentativeSampleCommand command);

    /**
     * Records the potato calibers and closes the pending qualification.
     *
     * @param command caliber percentages
     * @return the graded batch
     */
    Result<HarvestBatch, ApplicationError> handle(GradePotatoCalibersCommand command);

    /**
     * Records the coffee cupping and closes the pending qualification.
     *
     * @param command cupping scores
     * @return the cupped batch
     */
    Result<HarvestBatch, ApplicationError> handle(CupCoffeeCommand command);
}
