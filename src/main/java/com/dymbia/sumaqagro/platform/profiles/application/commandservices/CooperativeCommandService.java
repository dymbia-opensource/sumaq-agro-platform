package com.dymbia.sumaqagro.platform.profiles.application.commandservices;

import com.dymbia.sumaqagro.platform.profiles.domain.model.aggregates.Cooperative;
import com.dymbia.sumaqagro.platform.profiles.domain.model.commands.AddCooperativeMemberCommand;
import com.dymbia.sumaqagro.platform.profiles.domain.model.commands.InviteAgronomistCommand;
import com.dymbia.sumaqagro.platform.profiles.domain.model.commands.RegisterCooperativeCommand;
import com.dymbia.sumaqagro.platform.profiles.domain.model.entities.AgronomistInvitation;
import com.dymbia.sumaqagro.platform.profiles.domain.model.entities.CooperativeMember;
import com.dymbia.sumaqagro.platform.shared.application.result.ApplicationError;
import com.dymbia.sumaqagro.platform.shared.application.result.Result;

/**
 * Command service for the Cooperative aggregate.
 *
 * @since 1.0
 */
public interface CooperativeCommandService {

    /**
     * Registers a new cooperative.
     *
     * @param command the registration command
     * @return Result containing the created Cooperative or an ApplicationError
     */
    Result<Cooperative, ApplicationError> handle(RegisterCooperativeCommand command);

    /**
     * Adds a member to an existing cooperative.
     *
     * @param command the add member command
     * @return Result containing the created CooperativeMember or an ApplicationError
     */
    Result<CooperativeMember, ApplicationError> handle(AddCooperativeMemberCommand command);

    /**
     * Invites an agronomist to a cooperative.
     *
     * @param command the invitation command
     * @return Result containing the created AgronomistInvitation or an ApplicationError
     */
    Result<AgronomistInvitation, ApplicationError> handle(InviteAgronomistCommand command);
}