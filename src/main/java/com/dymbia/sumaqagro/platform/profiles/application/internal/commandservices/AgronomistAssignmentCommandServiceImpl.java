package com.dymbia.sumaqagro.platform.profiles.application.internal.commandservices;

import com.dymbia.sumaqagro.platform.profiles.application.commandservices.AgronomistAssignmentCommandService;
import com.dymbia.sumaqagro.platform.profiles.application.internal.outboundservices.acl.ExternalFieldManagementService;
import com.dymbia.sumaqagro.platform.profiles.application.internal.outboundservices.acl.ExternalIamService;
import com.dymbia.sumaqagro.platform.profiles.domain.model.aggregates.AgronomistAssignment;
import com.dymbia.sumaqagro.platform.profiles.domain.model.commands.AssignAgronomistToPlotCommand;
import com.dymbia.sumaqagro.platform.profiles.domain.model.commands.ReassignAgronomistToPlotCommand;
import com.dymbia.sumaqagro.platform.profiles.domain.repositories.AgronomistAssignmentRepository;
import com.dymbia.sumaqagro.platform.profiles.domain.repositories.CooperativeRepository;
import com.dymbia.sumaqagro.platform.shared.application.result.ApplicationError;
import com.dymbia.sumaqagro.platform.shared.application.result.Result;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AgronomistAssignmentCommandServiceImpl implements AgronomistAssignmentCommandService {

    private final AgronomistAssignmentRepository agronomistAssignmentRepository;
    private final CooperativeRepository cooperativeRepository;
    private final ExternalFieldManagementService externalFieldManagementService;
    private final ExternalIamService externalIamService;

    public AgronomistAssignmentCommandServiceImpl(AgronomistAssignmentRepository agronomistAssignmentRepository,
                                                  CooperativeRepository cooperativeRepository,
                                                  ExternalFieldManagementService externalFieldManagementService,
                                                  ExternalIamService externalIamService) {
        this.agronomistAssignmentRepository = agronomistAssignmentRepository;
        this.cooperativeRepository = cooperativeRepository;
        this.externalFieldManagementService = externalFieldManagementService;
        this.externalIamService = externalIamService;
    }

    // inherited javadoc
    @Override
    @Transactional
    public Result<AgronomistAssignment, ApplicationError> handle(AssignAgronomistToPlotCommand command) {
        try {
            var cooperativeOptional = cooperativeRepository.findById(command.cooperativeId());
            if (cooperativeOptional.isEmpty()) {
                return Result.failure(ApplicationError.notFound("Cooperative", command.cooperativeId().toString()));
            }
            var producerUserId = externalFieldManagementService.fetchProducerUserIdByPlotId(command.plotId());
            if (producerUserId.isEmpty()) {
                return Result.failure(ApplicationError.notFound("FieldPlot", command.plotId().toString()));
            }
            if (!cooperativeOptional.get().hasActiveMemberWithUserId(producerUserId.get())) {
                return Result.failure(ApplicationError.businessRuleViolation(
                        "plot-owner-must-be-member",
                        "The owner of the plot is not an active member of this cooperative"));
            }
            if (!externalIamService.isAgronomist(command.agronomistUserId())) {
                return Result.failure(ApplicationError.validationError(
                        "agronomistUserId", "The user is not registered as an agronomist"));
            }
            if (agronomistAssignmentRepository.existsActiveByPlotId(command.plotId())) {
                return Result.failure(ApplicationError.conflict(
                        "AgronomistAssignment", "Plot already has an active agronomist assignment"));
            }
            var assignment = new AgronomistAssignment(command, producerUserId.get());
            return Result.success(agronomistAssignmentRepository.save(assignment));
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.validationError("AgronomistAssignment", e.getMessage()));
        } catch (Exception e) {
            return Result.failure(ApplicationError.unexpected("Assign agronomist to plot", e.getMessage()));
        }
    }

    // inherited javadoc
    @Override
    @Transactional
    public Result<AgronomistAssignment, ApplicationError> handle(ReassignAgronomistToPlotCommand command) {
        try {
            var assignmentOptional = agronomistAssignmentRepository.findById(command.assignmentId());
            if (assignmentOptional.isEmpty()) {
                return Result.failure(ApplicationError.notFound(
                        "AgronomistAssignment", command.assignmentId().toString()));
            }
            if (!externalIamService.isAgronomist(command.agronomistUserId())) {
                return Result.failure(ApplicationError.validationError(
                        "agronomistUserId", "The user is not registered as an agronomist"));
            }
            var assignment = assignmentOptional.get().reassignTo(command.agronomistUserId());
            return Result.success(agronomistAssignmentRepository.save(assignment));
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.validationError("AgronomistAssignment", e.getMessage()));
        } catch (Exception e) {
            return Result.failure(ApplicationError.unexpected("Reassign agronomist", e.getMessage()));
        }
    }
}
