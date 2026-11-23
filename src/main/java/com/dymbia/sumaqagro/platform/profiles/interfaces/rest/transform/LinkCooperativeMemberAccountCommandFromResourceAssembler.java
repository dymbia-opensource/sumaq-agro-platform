package com.dymbia.sumaqagro.platform.profiles.interfaces.rest.transform;

import com.dymbia.sumaqagro.platform.profiles.domain.model.commands.LinkCooperativeMemberAccountCommand;
import com.dymbia.sumaqagro.platform.profiles.interfaces.rest.resources.LinkCooperativeMemberAccountResource;

public final class LinkCooperativeMemberAccountCommandFromResourceAssembler {

    private LinkCooperativeMemberAccountCommandFromResourceAssembler() {
    }

    public static LinkCooperativeMemberAccountCommand toCommandFromResource(Long cooperativeId,
                                                                            long directorUserId,
                                                                            Long memberId,
                                                                            LinkCooperativeMemberAccountResource resource) {
        return new LinkCooperativeMemberAccountCommand(cooperativeId, directorUserId, memberId, resource.email());
    }
}
