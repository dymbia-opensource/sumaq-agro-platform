package com.dymbia.sumaqagro.platform.subscriptions.domain.repositories;

import com.dymbia.sumaqagro.platform.subscriptions.domain.model.entities.Plan;
import com.dymbia.sumaqagro.platform.subscriptions.domain.model.valueobjects.PlanCode;

/**
 * Repository for subscription catalog plans.
 */
public interface PlanRepository {

    /**
     * Checks if a plan with the supplied code exists.
     * */
    boolean existsByCode(PlanCode code);

    /**
     * Inserts a new plan into the repository.
     * */
    void insert(Plan plan);
}
