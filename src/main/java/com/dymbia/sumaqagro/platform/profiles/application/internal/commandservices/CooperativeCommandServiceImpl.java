package com.dymbia.sumaqagro.platform.profiles.application.internal.commandservices;

import com.dymbia.sumaqagro.platform.profiles.application.commandservices.CooperativeCommandService;
import com.dymbia.sumaqagro.platform.profiles.domain.model.aggregates.Cooperative;
import com.dymbia.sumaqagro.platform.profiles.domain.model.commands.AddCooperativeMemberCommand;
import com.dymbia.sumaqagro.platform.profiles.domain.model.commands.InviteAgronomistCommand;
import com.dymbia.sumaqagro.platform.profiles.domain.model.commands.RegisterCooperativeCommand;
import com.dymbia.sumaqagro.platform.profiles.domain.model.entities.AgronomistInvitation;
import com.dymbia.sumaqagro.platform.profiles.domain.model.entities.CooperativeMember;
import com.dymbia.sumaqagro.platform.profiles.domain.repositories.CooperativeRepository;
import com.dymbia.sumaqagro.platform.shared.application.result.ApplicationError;
import com.dymbia.sumaqagro.platform.shared.application.result.Result;
import com.dymbia.sumaqagro.platform.subscriptions.interfaces.acl.SubscriptionsContextFacade;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of CooperativeCommandService.
 *
 * @since 1.0
 */
@Service
public class CooperativeCommandServiceImpl implements CooperativeCommandService {

    private final CooperativeRepository cooperativeRepository;
    private final SubscriptionsContextFacade subscriptions;

    public CooperativeCommandServiceImpl(
            CooperativeRepository cooperativeRepository, SubscriptionsContextFacade subscriptions
    ) {
        this.cooperativeRepository = cooperativeRepository;
        this.subscriptions = subscriptions;
    }

    @Override
    @Transactional
    public Result<Cooperative, ApplicationError> handle(RegisterCooperativeCommand command) {
        try {
            if (cooperativeRepository.existsByRuc(command.ruc())) {
                return Result.failure(ApplicationError.conflict("Cooperative", "RUC already registered"));
            }

            Cooperative cooperative = new Cooperative(command);
            Cooperative saved = cooperativeRepository.save(cooperative);

            return Result.success(saved);
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.validationError("Cooperative", e.getMessage()));
        } catch (Exception e) {
            return Result.failure(ApplicationError.unexpected("Cooperative registration", e.getMessage()));
        }
    }

    @Override
    @Transactional
    public Result<CooperativeMember, ApplicationError> handle(AddCooperativeMemberCommand command) {
        var limit = subscriptions.lockCooperativeMemberLimit(command.directorUserId());
        if (limit.isEmpty()) {
            return Result.failure(ApplicationError.businessRuleViolation(
                    "cooperative-pro-required", "A current Cooperative Pro subscription is required."));
        }
        try {
            var cooperativeOptional = cooperativeRepository.findById(command.cooperativeId());

            if (cooperativeOptional.isEmpty()
                    || !cooperativeOptional.get().getManagerUserId().equals(command.directorUserId())) {
                return Result.failure(ApplicationError.notFound("Cooperative", "id=" + command.cooperativeId()));
            }

            Cooperative cooperative = cooperativeOptional.get();
            if (cooperative.getMembers().size() >= limit.get()) {
                return Result.failure(ApplicationError.businessRuleViolation(
                        "cooperative-member-quota", "The cooperative reached its member quota."));
            }
            CooperativeMember member = cooperative.addMember(command);
            cooperativeRepository.save(cooperative);

            return Result.success(member);
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.validationError("CooperativeMember", e.getMessage()));
        } catch (Exception e) {
            return Result.failure(ApplicationError.unexpected("Add cooperative member", e.getMessage()));
        }
    }

    @Override
    @Transactional
    public Result<AgronomistInvitation, ApplicationError> handle(InviteAgronomistCommand command) {
        try {
            var cooperativeOptional = cooperativeRepository.findById(command.cooperativeId());

            if (cooperativeOptional.isEmpty()) {
                return Result.failure(ApplicationError.notFound("Cooperative", "id=" + command.cooperativeId()));
            }

            Cooperative cooperative = cooperativeOptional.get();
            AgronomistInvitation invitation = cooperative.inviteAgronomist(command);
            cooperativeRepository.save(cooperative);

            // TODO: send invitation email using NotificationSender (Brevo)

            return Result.success(invitation);
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.validationError("AgronomistInvitation", e.getMessage()));
        } catch (Exception e) {
            return Result.failure(ApplicationError.unexpected("Invite agronomist", e.getMessage()));
        }
    }
}
