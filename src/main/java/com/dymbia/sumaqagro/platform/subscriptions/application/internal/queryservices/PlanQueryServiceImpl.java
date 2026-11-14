package com.dymbia.sumaqagro.platform.subscriptions.application.internal.queryservices;

import com.dymbia.sumaqagro.platform.subscriptions.application.queryservices.PlanQueryService;
import com.dymbia.sumaqagro.platform.subscriptions.domain.model.entities.Plan;
import com.dymbia.sumaqagro.platform.subscriptions.domain.model.queries.GetAllPlansQuery;
import com.dymbia.sumaqagro.platform.subscriptions.domain.repositories.PlanRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/** Retrieves catalog plans through the domain repository. */
@Service
public class PlanQueryServiceImpl implements PlanQueryService {

private final PlanRepository planRepository;

public PlanQueryServiceImpl(PlanRepository planRepository) {
        this.planRepository = planRepository;
    }

    @Override
    public List<Plan> handle(GetAllPlansQuery query) {
        return planRepository.findAll();
    }
}
