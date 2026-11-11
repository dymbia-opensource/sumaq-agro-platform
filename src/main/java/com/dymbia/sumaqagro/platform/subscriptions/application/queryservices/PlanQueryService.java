package com.dymbia.sumaqagro.platform.subscriptions.application.queryservices;

import com.dymbia.sumaqagro.platform.subscriptions.domain.model.entities.Plan;
import com.dymbia.sumaqagro.platform.subscriptions.domain.model.queries.GetAllPlansQuery;

import java.util.List;

/**
 * Handles read operations for the subscription catalog.
 * */
public interface PlanQueryService {

    /**
     * Returns the plans available for consultation.
     * */
    List<Plan> handle(GetAllPlansQuery query);
}