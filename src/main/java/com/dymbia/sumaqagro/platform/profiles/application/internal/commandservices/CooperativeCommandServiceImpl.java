package com.dymbia.sumaqagro.platform.profiles.application.internal.commandservices;

import com.dymbia.sumaqagro.platform.profiles.application.commandservices.CooperativeCommandService;
import com.dymbia.sumaqagro.platform.profiles.domain.model.aggregates.Cooperative;
import com.dymbia.sumaqagro.platform.profiles.application.internal.outboundservices.acl.ExternalIamService;
import com.dymbia.sumaqagro.platform.profiles.domain.model.commands.AcceptAgronomistInvitationsCommand;
import com.dymbia.sumaqagro.platform.profiles.domain.model.commands.AddCooperativeMemberCommand;
import com.dymbia.sumaqagro.platform.profiles.domain.model.commands.InviteAgronomistCommand;
import com.dymbia.sumaqagro.platform.profiles.domain.model.commands.LinkCooperativeMemberAccountCommand;
import com.dymbia.sumaqagro.platform.profiles.domain.model.commands.RegisterCooperativeCommand;
import com.dymbia.sumaqagro.platform.profiles.domain.model.commands.UpdateCooperativeCommand;
import com.dymbia.sumaqagro.platform.profiles.domain.model.entities.AgronomistInvitation;
import com.dymbia.sumaqagro.platform.profiles.domain.model.entities.CooperativeMember;
import com.dymbia.sumaqagro.platform.profiles.domain.model.valueobjects.EmailAddress;
import com.dymbia.sumaqagro.platform.profiles.domain.repositories.CooperativeRepository;
import com.dymbia.sumaqagro.platform.shared.application.result.ApplicationError;
import com.dymbia.sumaqagro.platform.shared.application.result.Result;
import com.dymbia.sumaqagro.platform.subscriptions.interfaces.acl.SubscriptionsContextFacade;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class CooperativeCommandServiceImpl implements CooperativeCommandService {

    private final CooperativeRepository cooperativeRepository;
    private final SubscriptionsContextFacade subscriptions;
    private final ExternalIamService externalIamService;

    public CooperativeCommandServiceImpl(
            CooperativeRepository cooperativeRepository,
            SubscriptionsContextFacade subscriptions,
            ExternalIamService externalIamService
    ) {
        this.cooperativeRepository = cooperativeRepository;
        this.subscriptions = subscriptions;
        this.externalIamService = externalIamService;
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
            // The director gives the email of the producer account; the member is linked to its user id
            var accountError = farmerAccountError(command.userId(), command.email());
            if (accountError.isPresent()) {
                return Result.failure(accountError.get());
            }
            var linkedCommand = command.email() == null
                    ? command
                    : command.withUserId(externalIamService.findFarmerUserIdByEmail(command.email()).orElseThrow());
            var member = cooperative.addMember(linkedCommand);
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
            if (cooperativeOptional.isEmpty()
                    || !cooperativeOptional.get().getManagerUserId().equals(command.directorUserId())) {
                return Result.failure(ApplicationError.notFound("Cooperative", command.cooperativeId().toString()));
            }
            var cooperative = cooperativeOptional.get();
            // An agronomist who already has an account joins the technical team at once
            var agronomistUserId = externalIamService.findAgronomistUserIdByEmail(command.email()).orElse(null);
            var invitation = cooperative.inviteAgronomist(command, agronomistUserId);
            var savedCooperative = cooperativeRepository.save(cooperative);
            // The invitation email is sent by AgronomistInvitedEventHandler after this transaction commits
            return savedCooperative.findInvitationByToken(invitation.getInvitationToken())
                    .<Result<AgronomistInvitation, ApplicationError>>map(Result::success)
                    .orElseGet(() -> Result.failure(ApplicationError.notFound("AgronomistInvitation", invitation.getEmail())));
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.validationError("AgronomistInvitation", e.getMessage()));
        } catch (Exception e) {
            return Result.failure(ApplicationError.unexpected("Invite agronomist", e.getMessage()));
        }
    }

    // inherited javadoc
    @Override
    @Transactional
    public Result<CooperativeMember, ApplicationError> handle(LinkCooperativeMemberAccountCommand command) {
        try {
            var cooperativeOptional = cooperativeRepository.findById(command.cooperativeId());
            if (cooperativeOptional.isEmpty()
                    || !cooperativeOptional.get().getManagerUserId().equals(command.directorUserId())) {
                return Result.failure(ApplicationError.notFound("Cooperative", "id=" + command.cooperativeId()));
            }
            var accountError = farmerAccountError(null, command.email());
            if (accountError.isPresent()) {
                return Result.failure(accountError.get());
            }
            var userId = externalIamService.findFarmerUserIdByEmail(command.email()).orElseThrow();
            var cooperative = cooperativeOptional.get();
            cooperative.linkMemberAccount(command.memberId(), userId);
            var savedCooperative = cooperativeRepository.save(cooperative);
            return savedCooperative.getMembers().stream()
                    .filter(member -> command.memberId().equals(member.getId()))
                    .findFirst()
                    .<Result<CooperativeMember, ApplicationError>>map(Result::success)
                    .orElseGet(() -> Result.failure(
                            ApplicationError.notFound("CooperativeMember", command.memberId().toString())));
        } catch (NoSuchElementException e) {
            return Result.failure(ApplicationError.notFound("CooperativeMember", command.memberId().toString()));
        } catch (IllegalStateException e) {
            return Result.failure(ApplicationError.conflict("CooperativeMember", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.validationError("CooperativeMember", e.getMessage()));
        } catch (Exception e) {
            return Result.failure(ApplicationError.unexpected("Link cooperative member account", e.getMessage()));
        }
    }

    // inherited javadoc
    @Override
    @Transactional
    public int handle(AcceptAgronomistInvitationsCommand command) {
        var joined = 0;
        var email = new EmailAddress(command.email());
        for (var cooperative : cooperativeRepository.findAllWithPendingInvitationFor(email)) {
            if (cooperative.acceptInvitationsOf(command.email(), command.agronomistUserId())) {
                cooperativeRepository.save(cooperative);
                joined++;
            }
        }
        return joined;
    }

    /**
     * Checks that the account given for a member belongs to a producer.
     *
     * @param userId IAM user id sent by the client, if any
     * @param email  account email sent by the client, if any
     * @return the error to answer, empty when the account is valid or none was given
     */
    private Optional<ApplicationError> farmerAccountError(Long userId, String email) {
        if (email != null && externalIamService.findFarmerUserIdByEmail(email).isEmpty()) {
            return Optional.of(ApplicationError.businessRuleViolation(
                    "member-account-not-found", "No producer account uses this email."));
        }
        if (userId != null && !externalIamService.isFarmer(userId)) {
            return Optional.of(ApplicationError.businessRuleViolation(
                    "member-account-not-found", "The user is not a producer account."));
        }
        return Optional.empty();
    }
}
