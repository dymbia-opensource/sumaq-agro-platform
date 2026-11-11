package com.dymbia.sumaqagro.platform.subscriptions.infrastructure.persistence.jpa.assemblers;

import com.dymbia.sumaqagro.platform.subscriptions.domain.model.entities.Plan;
import com.dymbia.sumaqagro.platform.subscriptions.infrastructure.persistence.jpa.entities.PlanPersistenceEntity;

/** Maps catalog plans to their JPA representation. */
public final class PlanPersistenceAssembler {

    private PlanPersistenceAssembler() {
    }

    /** Maps a domain plan to a new persistence record. */
    public static PlanPersistenceEntity toPersistenceFromDomain(Plan plan) {
        return new PlanPersistenceEntity(
                plan.getCode(),
                plan.getMonthlyPrice(),
                plan.getAnnualPrice(),
                plan.getRegisteredPlotLimit(),
                plan.getProducerLimit(),
                plan.getSupervisedPlotLimit()
        );
    }
}
