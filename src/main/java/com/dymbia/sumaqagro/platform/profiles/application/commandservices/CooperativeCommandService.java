package com.dymbia.sumaqagro.platform.profiles.application.commandservices;

import com.dymbia.sumaqagro.platform.profiles.domain.model.aggregates.Cooperative;
import com.dymbia.sumaqagro.platform.profiles.domain.model.commands.AcceptAgronomistInvitationsCommand;
import com.dymbia.sumaqagro.platform.profiles.domain.model.commands.AddCooperativeMemberCommand;
import com.dymbia.sumaqagro.platform.profiles.domain.model.commands.InviteAgronomistCommand;
import com.dymbia.sumaqagro.platform.profiles.domain.model.commands.LinkCooperativeMemberAccountCommand;
import com.dymbia.sumaqagro.platform.profiles.domain.model.commands.RegisterCooperativeCommand;
import com.dymbia.sumaqagro.platform.profiles.domain.model.commands.UpdateCooperativeCommand;
import com.dymbia.sumaqagro.platform.profiles.domain.model.entities.AgronomistInvitation;
import com.dymbia.sumaqagro.platform.profiles.domain.model.entities.CooperativeMember;
import com.dymbia.sumaqagro.platform.shared.application.result.ApplicationError;
import com.dymbia.sumaqagro.platform.shared.application.result.Result;

/**
 * Write operations of the {@link Cooperative} aggregate (and its members and invitations).
 *
 * @since 1.0
 */
public interface CooperativeCommandService {
    Result<Cooperative, ApplicationError> handle(RegisterCooperativeCommand command);

    Result<Cooperative, ApplicationError> handle(UpdateCooperativeCommand command);

    Result<CooperativeMember, ApplicationError> handle(AddCooperativeMemberCommand command);

    Result<AgronomistInvitation, ApplicationError> handle(InviteAgronomistCommand command);

    Result<CooperativeMember, ApplicationError> handle(LinkCooperativeMemberAccountCommand command);

    /**
     * Accepts the pending invitations sent to the email of a new agronomist account.
     *
     * @param command the agronomist account
     * @return how many cooperatives the agronomist joined
     */
    int handle(AcceptAgronomistInvitationsCommand command);
}
