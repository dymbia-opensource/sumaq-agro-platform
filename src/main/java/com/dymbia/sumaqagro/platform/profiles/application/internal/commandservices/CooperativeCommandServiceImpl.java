package com.dymbia.sumaqagro.platform.profiles.application.internal.commandservices;

import com.dymbia.sumaqagro.platform.profiles.application.commandservices.CooperativeCommandService;
import com.dymbia.sumaqagro.platform.profiles.domain.model.aggregates.Cooperative;
import com.dymbia.sumaqagro.platform.profiles.domain.model.commands.AddCooperativeMemberCommand;
import com.dymbia.sumaqagro.platform.profiles.domain.model.commands.InviteAgronomistCommand;
import com.dymbia.sumaqagro.platform.profiles.domain.model.commands.RegisterCooperativeCommand;
import com.dymbia.sumaqagro.platform.profiles.domain.model.commands.UpdateCooperativeCommand;
import com.dymbia.sumaqagro.platform.profiles.domain.model.entities.AgronomistInvitation;
import com.dymbia.sumaqagro.platform.profiles.domain.model.entities.CooperativeMember;
import com.dymbia.sumaqagro.platform.profiles.domain.repositories.CooperativeRepository;
import com.dymbia.sumaqagro.platform.shared.application.result.ApplicationError;
import com.dymbia.sumaqagro.platform.shared.application.result.Result;
import com.dymbia.sumaqagro.platform.subscriptions.interfaces.acl.SubscriptionsContextFacade;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    // inherited javadoc
    @Override
    @Transactional
    public Result<Cooperative, ApplicationError> handle(RegisterCooperativeCommand command) {
        try {
            if (cooperativeRepository.existsByRuc(command.ruc().trim())) {
                return Result.failure(ApplicationError.conflict(
                        "Cooperative", "RUC '%s' is already registered".formatted(command.ruc())));
            }
            var cooperative = new Cooperative(command);
            return Result.success(cooperativeRepository.save(cooperative));
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.validationError("Cooperative", e.getMessage()));
        } catch (Exception e) {
            return Result.failure(ApplicationError.unexpected("Cooperative registration", e.getMessage()));
        }
    }

    // inherited javadoc
    @Override
    @Transactional
    public Result<Cooperative, ApplicationError> handle(UpdateCooperativeCommand command) {
        try {
            var cooperativeOptional = cooperativeRepository.findById(command.cooperativeId());
            if (cooperativeOptional.isEmpty()) {
                return Result.failure(ApplicationError.notFound("Cooperative", command.cooperativeId().toString()));
            }
            if (cooperativeRepository.existsByRucAndIdIsNot(command.ruc().trim(), command.cooperativeId())) {
                return Result.failure(ApplicationError.conflict(
                        "Cooperative", "RUC '%s' belongs to another cooperative".formatted(command.ruc())));
            }
            var cooperative = cooperativeOptional.get().updateInstitutionalData(command);
            return Result.success(cooperativeRepository.save(cooperative));
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.validationError("Cooperative", e.getMessage()));
        } catch (Exception e) {
            return Result.failure(ApplicationError.unexpected("Cooperative update", e.getMessage()));
        }
    }

    // inherited javadoc
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
            var member = cooperative.addMember(command);
            var savedCooperative = cooperativeRepository.save(cooperative);
            // Read the member again from the saved cooperative so the response carries its id.
            return savedCooperative.findMemberByDni(member.getDni().value())
                    .<Result<CooperativeMember, ApplicationError>>map(Result::success)
                    .orElseGet(() -> Result.failure(ApplicationError.notFound("CooperativeMember", member.getDni().value())));
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.validationError("CooperativeMember", e.getMessage()));
        } catch (Exception e) {
            return Result.failure(ApplicationError.unexpected("Add cooperative member", e.getMessage()));
        }
    }

    // inherited javadoc
    @Override
    @Transactional
    public Result<AgronomistInvitation, ApplicationError> handle(InviteAgronomistCommand command) {
        try {
            var cooperativeOptional = cooperativeRepository.findById(command.cooperativeId());
            if (cooperativeOptional.isEmpty()) {
                return Result.failure(ApplicationError.notFound("Cooperative", command.cooperativeId().toString()));
            }
            var cooperative = cooperativeOptional.get();
            var invitation = cooperative.inviteAgronomist(command);
            var savedCooperative = cooperativeRepository.save(cooperative);
            // TODO (en revisión con el equipo): enviar el correo de invitación con Brevo desde un event handler.
            return savedCooperative.findInvitationByToken(invitation.getInvitationToken())
                    .<Result<AgronomistInvitation, ApplicationError>>map(Result::success)
                    .orElseGet(() -> Result.failure(ApplicationError.notFound("AgronomistInvitation", invitation.getEmail())));
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.validationError("AgronomistInvitation", e.getMessage()));
        } catch (Exception e) {
            return Result.failure(ApplicationError.unexpected("Invite agronomist", e.getMessage()));
        }
    }
}
