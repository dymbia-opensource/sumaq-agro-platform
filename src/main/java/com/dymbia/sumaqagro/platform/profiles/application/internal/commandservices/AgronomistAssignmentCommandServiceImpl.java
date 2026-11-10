package com.dymbia.sumaqagro.platform.profiles.application.internal.commandservices;

import com.dymbia.sumaqagro.platform.profiles.application.commandservices.AgronomistAssignmentCommandService;
import com.dymbia.sumaqagro.platform.profiles.domain.model.aggregates.AgronomistAssignment;
import com.dymbia.sumaqagro.platform.profiles.domain.model.commands.AssignAgronomistToPlotCommand;
import com.dymbia.sumaqagro.platform.profiles.domain.repositories.AgronomistAssignmentRepository;
import com.dymbia.sumaqagro.platform.shared.application.result.ApplicationError;
import com.dymbia.sumaqagro.platform.shared.application.result.Result;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of AgronomistAssignmentCommandService.
 *
 * @since 1.0
 */
@Service
public class AgronomistAssignmentCommandServiceImpl implements AgronomistAssignmentCommandService {

    private final AgronomistAssignmentRepository agronomistAssignmentRepository;

    public AgronomistAssignmentCommandServiceImpl(AgronomistAssignmentRepository agronomistAssignmentRepository) {
        this.agronomistAssignmentRepository = agronomistAssignmentRepository;
    }

    @Override
    @Transactional
    public Result<AgronomistAssignment, ApplicationError> handle(AssignAgronomistToPlotCommand command) {
        try {
            if (agronomistAssignmentRepository.existsActiveByPlotId(command.plotId())) {
                return Result.failure(ApplicationError.conflict(
                        "AgronomistAssignment",
                        "Plot already has an active agronomist assignment"
                ));
            }

            AgronomistAssignment assignment = new AgronomistAssignment(command);
            AgronomistAssignment saved = agronomistAssignmentRepository.save(assignment);

            return Result.success(saved);
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.validationError("AgronomistAssignment", e.getMessage()));
        } catch (Exception e) {
            return Result.failure(ApplicationError.unexpected("Assign agronomist to plot", e.getMessage()));
        }
    }
}