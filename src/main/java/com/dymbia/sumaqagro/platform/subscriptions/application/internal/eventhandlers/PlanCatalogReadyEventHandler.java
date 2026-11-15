package com.dymbia.sumaqagro.platform.subscriptions.application.internal.eventhandlers;

import com.dymbia.sumaqagro.platform.subscriptions.application.commandservices.PlanCommandService;
import com.dymbia.sumaqagro.platform.subscriptions.domain.commands.SeedPlansCommand;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/** Initializes the subscription catalog when the application is ready. */
@Component
public class PlanCatalogReadyEventHandler {

    private final PlanCommandService planCommandService;

    public PlanCatalogReadyEventHandler(PlanCommandService planCommandService) {
        this.planCommandService = planCommandService;
    }

    /** Seeds any missing catalog plans at application startup. */
    @EventListener(ApplicationReadyEvent.class)
    public void seedPlans() {
        planCommandService.handle(new SeedPlansCommand());
    }
}
