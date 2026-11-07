package com.dymbia.sumaqagro.platform.profiles.application.internal.commandservices;

import com.dymbia.sumaqagro.platform.profiles.application.commandservices.AgronomistAssignmentCommandService;
import com.dymbia.sumaqagro.platform.profiles.domain.model.aggregates.AgronomistAssignment;
import com.dymbia.sumaqagro.platform.profiles.domain.model.commands.AssignAgronomistToPlotCommand;
import com.dymbia.sumaqagro.platform.profiles.infrastructure.persistence.jpa.repositories.AgronomistAssignmentPersistenceRepository;
import com.dymbia.sumaqagro.platform.shared.application.result.ApplicationError;
import com.dymbia.sumaqagro.platform.shared.application.result.Result;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AgronomistAssignmentCommandServiceImpl implements AgronomistAssignmentCommandService {

    private final AgronomistAssignmentPersistenceRepository agronomistAssignmentPersistenceRepository;

    public AgronomistAssignmentCommandServiceImpl(AgronomistAssignmentPersistenceRepository agronomistAssignmentPersistenceRepository) {
        this.agronomistAssignmentPersistenceRepository = agronomistAssignmentPersistenceRepository;
    }

    @Override
    @Transactional
    public Result<AgronomistAssignment, ApplicationError> handle(AssignAgronomistToPlotCommand command) {
        try {
            if (agronomistAssignmentPersistenceRepository.existsByPlotIdAndActiveTrue(command.plotId())) {
                return Result.failure(ApplicationError.conflict(
                        "AgronomistAssignment",
                        "Plot already has an active agronomist assignment"
                ));
            }

            AgronomistAssignment assignment = new AgronomistAssignment(command);
            AgronomistAssignment saved = agronomistAssignmentPersistenceRepository.save(assignment);

            return Result.success(saved);
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.validationError("AgronomistAssignment", e.getMessage()));
        } catch (Exception e) {
            return Result.failure(ApplicationError.unexpected("Assign agronomist to plot", e.getMessage()));
        }
    }
}