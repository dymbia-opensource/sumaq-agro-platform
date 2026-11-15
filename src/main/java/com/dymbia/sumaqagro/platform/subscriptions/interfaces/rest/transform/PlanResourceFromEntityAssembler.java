package com.dymbia.sumaqagro.platform.subscriptions.interfaces.rest.transform;

import com.dymbia.sumaqagro.platform.subscriptions.domain.model.entities.Plan;
import com.dymbia.sumaqagro.platform.subscriptions.interfaces.rest.resources.PlanResource;


/**
 * Maps one plan using its commercial code as its public identifier.
 */
public final class PlanResourceFromEntityAssembler {

    private PlanResourceFromEntityAssembler() {
    }

    /**
     * Maps one plan using its commercial code as its public identifier.
     */
    public static PlanResource toResourceFromEntity(Plan plan) {
        return new PlanResource(
                plan.getCode().name(),
                plan.getMonthlyPrice(),
                plan.getAnnualPrice(),
                "PEN",
                plan.getRegisteredPlotLimit(),
                plan.getProducerLimit(),
                plan.getSupervisedPlotLimit()
        );
    }
}