package com.dymbia.sumaqagro.platform.profiles.application.internal.commandservices;

import com.dymbia.sumaqagro.platform.profiles.application.commandservices.CooperativeCommandService;
import com.dymbia.sumaqagro.platform.profiles.domain.model.aggregates.Cooperative;
import com.dymbia.sumaqagro.platform.profiles.domain.model.commands.AddCooperativeMemberCommand;
import com.dymbia.sumaqagro.platform.profiles.domain.model.commands.InviteAgronomistCommand;
import com.dymbia.sumaqagro.platform.profiles.domain.model.commands.RegisterCooperativeCommand;
import com.dymbia.sumaqagro.platform.profiles.domain.model.entities.AgronomistInvitation;
import com.dymbia.sumaqagro.platform.profiles.domain.model.entities.CooperativeMember;
import com.dymbia.sumaqagro.platform.profiles.infrastructure.persistence.jpa.repositories.CooperativePersistenceRepository;
import com.dymbia.sumaqagro.platform.shared.application.result.ApplicationError;
import com.dymbia.sumaqagro.platform.shared.application.result.Result;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CooperativeCommandServiceImpl implements CooperativeCommandService {

    private final CooperativePersistenceRepository cooperativePersistenceRepository;

    public CooperativeCommandServiceImpl(CooperativePersistenceRepository cooperativePersistenceRepository) {
        this.cooperativePersistenceRepository = cooperativePersistenceRepository;
    }

    @Override
    @Transactional
    public Result<Cooperative, ApplicationError> handle(RegisterCooperativeCommand command) {
        try {
            if (cooperativePersistenceRepository.existsByRuc(command.ruc())) {
                return Result.failure(ApplicationError.conflict("Cooperative", "RUC already registered"));
            }

            Cooperative cooperative = new Cooperative(command);
            Cooperative saved = cooperativePersistenceRepository.save(cooperative);

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
        try {
            var cooperativeOptional = cooperativePersistenceRepository.findById(command.cooperativeId());

            if (cooperativeOptional.isEmpty()) {
                return Result.failure(ApplicationError.notFound("Cooperative", "id=" + command.cooperativeId()));
            }

            Cooperative cooperative = cooperativeOptional.get();
            CooperativeMember member = cooperative.addMember(command);
            cooperativePersistenceRepository.save(cooperative);

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
            var cooperativeOptional = cooperativePersistenceRepository.findById(command.cooperativeId());

            if (cooperativeOptional.isEmpty()) {
                return Result.failure(ApplicationError.notFound("Cooperative", "id=" + command.cooperativeId()));
            }

            Cooperative cooperative = cooperativeOptional.get();
            AgronomistInvitation invitation = cooperative.inviteAgronomist(command);
            cooperativePersistenceRepository.save(cooperative);

            // TODO: send invitation email using NotificationSender (Brevo)

            return Result.success(invitation);
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.validationError("AgronomistInvitation", e.getMessage()));
        } catch (Exception e) {
            return Result.failure(ApplicationError.unexpected("Invite agronomist", e.getMessage()));
        }
    }
}