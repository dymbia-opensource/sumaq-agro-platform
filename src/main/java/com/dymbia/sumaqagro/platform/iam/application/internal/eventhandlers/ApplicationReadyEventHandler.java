package com.dymbia.sumaqagro.platform.iam.application.internal.eventhandlers;

import com.dymbia.sumaqagro.platform.iam.application.commandservices.RoleCommandService;
import com.dymbia.sumaqagro.platform.iam.domain.model.commands.SeedRolesCommand;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class ApplicationReadyEventHandler {

    private final RoleCommandService roleCommandService;

    public ApplicationReadyEventHandler(RoleCommandService roleCommandService) {
        this.roleCommandService = roleCommandService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void seedRoles() {
        roleCommandService.handle(new SeedRolesCommand());
    }
}