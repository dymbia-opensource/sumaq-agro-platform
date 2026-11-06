package com.dymbia.sumaqagro.platform.iam.application.commandservices;

import com.dymbia.sumaqagro.platform.iam.domain.model.commands.SeedRolesCommand;

public interface RoleCommandService {
    void handle(SeedRolesCommand command);
}
