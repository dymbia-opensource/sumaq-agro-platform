package com.dymbia.sumaqagro.platform.subscriptions.application.commandservices;
import com.dymbia.sumaqagro.platform.subscriptions.domain.commands.SeedPlansCommand;

/**
 * Service for handling commands related to subscription catalog plans.
 */
public interface PlanCommandService {

    /**
     * Seeds the subscription catalog with the default plans.
     */
    void handle(SeedPlansCommand command);
}
