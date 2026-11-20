package com.dymbia.sumaqagro.platform.profiles.application.internal.commandservices;

import com.dymbia.sumaqagro.platform.profiles.application.commandservices.AgronomistAssignmentCommandService;
import com.dymbia.sumaqagro.platform.profiles.application.internal.outboundservices.acl.ExternalFarmingService;
import com.dymbia.sumaqagro.platform.profiles.application.internal.outboundservices.acl.ExternalIamService;
import com.dymbia.sumaqagro.platform.profiles.domain.model.aggregates.AgronomistAssignment;
import com.dymbia.sumaqagro.platform.profiles.domain.model.commands.AssignAgronomistToPlotCommand;
import com.dymbia.sumaqagro.platform.profiles.domain.model.commands.ReassignAgronomistToPlotCommand;
import com.dymbia.sumaqagro.platform.profiles.domain.repositories.AgronomistAssignmentRepository;
import com.dymbia.sumaqagro.platform.profiles.domain.repositories.CooperativeRepository;
import com.dymbia.sumaqagro.platform.shared.application.result.ApplicationError;
import com.dymbia.sumaqagro.platform.shared.application.result.Result;
import com.dymbia.sumaqagro.platform.subscriptions.interfaces.acl.SubscriptionsContextFacade;

import org.springframework.transaction.annotation.Isolation;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AgronomistAssignmentCommandServiceImpl implements AgronomistAssignmentCommandService {

    private final AgronomistAssignmentRepository agronomistAssignmentRepository;
    private final CooperativeRepository cooperativeRepository;
    private final ExternalFarmingService externalFarmingService;
    private final ExternalIamService externalIamService;
    private final SubscriptionsContextFacade subscriptions;

    public AgronomistAssignmentCommandServiceImpl(AgronomistAssignmentRepository agronomistAssignmentRepository,
                                                  CooperativeRepository cooperativeRepository,
                                                  ExternalFarmingService externalFarmingService,
                                                  ExternalIamService externalIamService,
                                                  SubscriptionsContextFacade subscriptions) {
        this.agronomistAssignmentRepository = agronomistAssignmentRepository;
        this.cooperativeRepository = cooperativeRepository;
        this.externalFarmingService = externalFarmingService;
        this.externalIamService = externalIamService;
        this.subscriptions = subscriptions;

    }

    // inherited javadoc
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Result<AgronomistAssignment, ApplicationError> handle(AssignAgronomistToPlotCommand command) {
        try {
            var cooperativeOptional = cooperativeRepository.findById(command.cooperativeId());
            if (cooperativeOptional.isEmpty()) {
                return Result.failure(ApplicationError.notFound("Cooperative", command.cooperativeId().toString()));
            }
            var producerUserId = externalFarmingService.fetchProducerUserIdByPlotId(command.plotId());
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
            var quotaError = checkAdvisorQuota(command.agronomistUserId());
            if (quotaError.isPresent()) {
                return Result.failure(quotaError.get());
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
    @Transactional(isolation = Isolation.READ_COMMITTED)
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
            var current = assignmentOptional.get();
            if (!command.agronomistUserId().equals(current.getAgronomistUserId())) {
                var quotaError = checkAdvisorQuota(command.agronomistUserId());
                if (quotaError.isPresent()) {
                    return Result.failure(quotaError.get());
                }
            }
            var assignment = current.reassignTo(command.agronomistUserId());
            return Result.success(agronomistAssignmentRepository.save(assignment));
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.validationError("AgronomistAssignment", e.getMessage()));
        } catch (Exception e) {
            return Result.failure(ApplicationError.unexpected("Reassign agronomist", e.getMessage()));
        }
    }

    private Optional<ApplicationError> checkAdvisorQuota(Long agronomistUserId) {
        var limit = subscriptions.lockAdvisorPlotLimit(agronomistUserId);
        if (limit.isEmpty()) {
            return Optional.of(ApplicationError.businessRuleViolation(
                    "technical-advisor-plan-required",
                    "A current Technical Advisor subscription is required"));
        }

        long activeAssignments =
                agronomistAssignmentRepository.countActiveByAgronomistUserId(agronomistUserId);
        if (activeAssignments >= limit.get()) {
            return Optional.of(ApplicationError.businessRuleViolation(
                    "advisor-plot-quota",
                    "The agronomist has reached the active plot assignment limit"));
        }

        return Optional.empty();
    }
}
